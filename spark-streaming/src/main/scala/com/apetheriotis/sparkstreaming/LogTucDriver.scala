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
import com.twitter.storehaus.redis._
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.apetheriotis.sparkstreaming.db.RedisStorage
import com.twitter.util.Future
import com.apetheriotis.sparkstreaming.streams.{DbLogsManager, LoadBalancerManager, HttpStatusCodeManager}

/**
 * @author Angelos Petheriotis
 */
object LogTucDriver {

  val KAFKA_WEB_APP_LOGS_TOPIC = "WebServerLogs"
  val KAFKA_DB_LOGS_TOPIC = "DBServerLogs"
  val envConf = ConfigFactory.load()
  val httpStatusCodeManager = new HttpStatusCodeManager()
  val loadBalancerManager = new LoadBalancerManager()
  val dbLogsManager = new DbLogsManager()


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

    // Web app Logs
    //    val kafkaInputs = (1 to envConf.getInt("noSparkSlaves")).map {
    //      _ =>
    //        KafkaUtils.createStream[String, String, StringDecoder,
    //          StringDecoder](ssc, kafkaParams, Map(KAFKA_WEB_APP_LOGS_TOPIC -> 1),
    //            StorageLevel.MEMORY_ONLY_SER_2).map(_._2)
    //    }
    //    val lines = ssc.union(kafkaInputs).repartition(envConf.getInt("noSparkSlaves"))
    //    httpStatusCodeManager.countStatusCodes(lines)
    //    loadBalancerManager.countRequestsPerServer(lines)

    // Database Logs
    val dbLogsKafkaStreams = (1 to envConf.getInt("noSparkSlaves")).map {
      _ =>
        KafkaUtils.createStream[String, String, StringDecoder,
          StringDecoder](ssc, kafkaParams, Map(KAFKA_DB_LOGS_TOPIC -> 1),
            StorageLevel.MEMORY_ONLY_SER_2).map(_._2)
    }
    val dbLogs = ssc.union(dbLogsKafkaStreams).repartition(envConf.getInt("noSparkSlaves"))
    dbLogsManager.countDBQueries(dbLogs)

    ssc.start()
    ssc.awaitTermination()
  }


}
