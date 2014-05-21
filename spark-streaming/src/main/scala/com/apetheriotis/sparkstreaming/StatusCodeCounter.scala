package com.apetheriotis.sparkstreaming

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel
import kafka.serializer.StringDecoder
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.dstream.DStream
import scala.util.Try
import com.mongodb.casbah.Imports._

/**
 * @author Angelos Petheriotis
 */
object StatusCodeCounter {

  val KAFKA_WEB_APP_LOGS_TOPIC = "LogTUC"

  val envConf = ConfigFactory.load()
  val mongoClient = MongoClient(envConf.getString("mongoIP"), 27017)
  val coll = mongoClient("LogTUC")

  def main(args: Array[String]) {

    // Setup kafka parameters
    val kafkaParams = Map[String, String](
      "zookeeper.connect" -> envConf.getString("zookeepers"),
      "group.id" -> "LogTucSpark",
      "zookeeper.connection.timeout.ms" -> "10000",
      "auto.commit.interval.ms" -> "10000",
      "auto.offset.reset" -> "largest")

    // Set logging level
    SparkLogging.setStreamingLogLevels()

    // Create context
    val ssc = new StreamingContext(envConf.getString("sparkMaster"), "LogTUC-Streaming", Seconds(2),
      "/opt/spark/spark-0.9.0-incubating/", StreamingContext.jarOfClass(this.getClass))

    // Fix error "No FileSystem for scheme: hdfs" with the following:
    val hadoopConfig = ssc.sparkContext.hadoopConfiguration
    hadoopConfig.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName())
    hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName())

    ssc.checkpoint(envConf.getString("hdfsURL") + "/checkpoints")

    // Use multiple streams for each kafka stream. If only stream is used, spark will use just one machine
    // Multiple kafka streams for Web App Logs
    val kafkaInputs = (1 to envConf.getInt("noSparkSlaves")).map {
      _ =>
        KafkaUtils.createStream[String, String, StringDecoder,
          StringDecoder](ssc, kafkaParams, Map(KAFKA_WEB_APP_LOGS_TOPIC -> 1),
            StorageLevel.MEMORY_ONLY_SER_2).map(_._2)
    }

    val lines = ssc.union(kafkaInputs).repartition(envConf.getInt("noSparkSlaves"))

    // Count by status code with window
    countStatusCodes(lines)
    countRequestsPerServer(lines)

    ssc.start()

    ssc.awaitTermination()
  }


  /**
   * Counts status codes as captured from stream every 2 seconds for the last 2 seconds
   * @param stream the DStream to read from
   */
  def countStatusCodes(stream: DStream[String]) {
    val statusCodes = stream.map(_.split("___")(2)) // 2 is the status code
    val pairs = statusCodes.map(statusCode => (statusCode, 1))
    val statusCodesCounts = pairs.reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2), 3) // TODO check that 3 here
    //    statusCodesCounts.print()
    statusCodesCounts.saveAsTextFiles(envConf.getString("hdfsURL") + "/status_codes/job")
  }

  /**
   * Counts instance ids as captured from stream every 2 seconds for the last 2 seconds and saves to datastore
   * @param stream the DStream to read from
   */
  def countRequestsPerServer(stream: DStream[String]) {
    val instanceIds = stream.map(_.split("___")(7)) // 7 is the instance id
    val instanceIdsCounts = instanceIds.countByValueAndWindow(Seconds(2), Seconds(2), 3) // TODO check that 3 here
    instanceIdsCounts.print()
    instanceIdsCounts.saveAsTextFiles(envConf.getString("hdfsURL") + "/instance_ids/job")

    // Zero all values. Big bakalia
    var totalIds = 0L
    var totalRequests = 0L
    var average = 0L
    var entry = MongoDBObject()
    var instancesAggregated: Map[String, Double] = Map()
    var instancesAggregatedOutsiders: Map[String, Boolean] = Map()
    instanceIdsCounts.foreachRDD(rdd => {
      rdd.collect()
      totalIds = 0L
      totalRequests = 0L
      average = 1L
      entry = MongoDBObject()
      instancesAggregated = Map()
      instancesAggregatedOutsiders = Map()
    })

    // Compute average
    instanceIdsCounts.foreachRDD(rdd => {
      val instanceIdsInRDD = rdd.collect()
      instanceIdsInRDD.foreach(id => {
        totalIds = totalIds + 1
        totalRequests = totalRequests + id._2
      })
      average = Try(totalRequests / totalIds).getOrElse(0L)
      println("Average: " + average)
    })

    // Find outsiders according to the threshold and save to db
    instanceIdsCounts.foreachRDD((rdd, time) => {
      entry = MongoDBObject()
      entry.put("time", time.milliseconds)
      val instanceIdsInRDD = rdd.collect()
      instanceIdsInRDD.foreach(id => {
        val percentage = (id._2 * 100) / average
        instancesAggregated += (id._1 -> percentage)
        if (percentage > 120 || percentage < 80) {
          instancesAggregatedOutsiders += (id._1 -> true)
        } else {
          instancesAggregatedOutsiders += (id._1 -> false)
        }
      })
      entry.put("instancesAggregated", instancesAggregated)
      entry.put("instancesAggregatedOutsiders", instancesAggregatedOutsiders)
      coll.getCollection("instanceIds").insert(entry)
    })


  }

}
