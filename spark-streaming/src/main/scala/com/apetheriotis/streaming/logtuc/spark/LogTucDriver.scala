package com.apetheriotis.streaming.logtuc.spark

import com.twitter.algebird.CMSHasherImplicits._
import com.twitter.algebird.TopPctCMS
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Spark driver for Logtuc app.
 * Logtuc reads logs from kafka and estimates count for various metrics using count min sketch structure.
 * Results are being saved to a redis instance.
 *
 * @author Angelos Petheriotis
 */
object LogTucDriver {

  val WEB_SERVER_LOGS_TOPIC = "WebServerLogs"

  // CMS parameters
  val DELTA = 1E-3
  val EPS = 0.01
  val SEED = 1
  val TOP_PERCENTAGE = 0.0001
  val TOP_K = 100

  def main(args: Array[String]) {

    // Process input args
    if (args.length < 5) {
      System.err.println("Usage: <zookeepers> <kafkaParallelism> <threads> <batch interval> <repartitions>")
      System.exit(1)
    }

    // Decode input vars
    val Array(zookeeper, kafkaParallelism, threads, interval, repartitions) = args

    // Create streaming context
    SparkLogging.setStreamingLogLevels()
    val sparkConf = new SparkConf().setAppName("LogTuc")
    val ssc = new StreamingContext(sparkConf, Seconds(interval.toInt))

    // Create cms structure
    val statusCodesCms = TopPctCMS.monoid[Long](EPS, DELTA, SEED, TOP_PERCENTAGE)
    val requestsPerServerCms = TopPctCMS.monoid[Long](EPS, DELTA, SEED, TOP_PERCENTAGE)

    // Set up the input DStream to read from Kafka (in parallel)
    val kafkaStream = {
      val kafkaParams = Map[String, String](
        "zookeeper.connect" -> zookeeper,
        "group.id" -> "LogTuc",
        "zookeeper.connection.timeout.ms" -> "10000",
        "auto.commit.interval.ms" -> "10000",
        "auto.offset.reset" -> "smallest")
      val streams = (1 to kafkaParallelism.toInt) map { _ =>
        KafkaUtils.createStream[String, String, StringDecoder,
          StringDecoder](ssc, kafkaParams, Map(WEB_SERVER_LOGS_TOPIC -> threads.toInt), StorageLevel.MEMORY_ONLY_SER).map(_._2)
      }
      val unifiedStream = ssc.union(streams)
      unifiedStream.map { x => x }
      unifiedStream.repartition(repartitions.toInt)
    }

    // Estimate frequency for each status codes
    val statusCodes = kafkaStream.map(log => (log.split("___")(2).toLong, 1)) // 3rd value is the status code
    val estimatedStatusCodesResults = statusCodes.mapPartitions(statusCodez => {
        statusCodez.map(statusCode => statusCodesCms.create(statusCode._1))
      }).reduce(_ ++ _)

    // Extract estimations for status codes
    estimatedStatusCodesResults.foreachRDD(rdd => {
      if (rdd.count() != 0) {
        val partial = rdd.first()
        val partialTopK = partial.heavyHitters.map(id =>
          (id, partial.frequency(id).estimate)).toSeq.sortBy(_._2).reverse.slice(0, TOP_K)
        HttpPostHelper.sendStatusCodesEstimations(partialTopK)
      }
    })


    // Estimate requests per server
    val webServers = kafkaStream.map(log => (log.split("___")(7).toLong, 1)) // 8th value is the web server id
    val estimatedRequestsPerServer = webServers.mapPartitions(webServer => {
        webServer.map(webServerId => requestsPerServerCms.create(webServerId._1))
      }).reduce(_ ++ _)

    // Extract estimations for each web server
    estimatedRequestsPerServer.foreachRDD(rdd => {
      if (rdd.count() != 0) {
        val partial = rdd.first()
        val partialTopK = partial.heavyHitters.map(id =>
          (id, partial.frequency(id).estimate)).toSeq.sortBy(_._2).reverse.slice(0, TOP_K)
        HttpPostHelper.sendRequestsPerServerEstimations(partialTopK)
      }
    })

    // Start spark job
    ssc.start()
    ssc.awaitTermination()


  }


}
