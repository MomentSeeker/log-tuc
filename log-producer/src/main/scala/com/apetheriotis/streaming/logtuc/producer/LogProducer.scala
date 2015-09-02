package com.apetheriotis.streaming.logtuc.producer

import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

import scala.collection.mutable.ArrayBuffer

/**
 * Creates web and db like logs and sends them to kafka.
 * @author Angelos Petheriotis
 */
object LogProducer {

  val WEB_LOGS_TOPIC = "WebServerLogs"
  val DB_LOGS_TOPIC = "DbServerLogs"

  def main(args: Array[String]) {

    // Get parameters from input
    if (args.length < 3) {
      System.err.println("Usage: <brokers> <sleep> <async>")
      System.exit(1)
    }

    // Print a sample log for each type (web and db)
    println(WebAppLogGenerator.createWebServerLog())
    println(DBLogGenerator.getDBServerLog())

    val Array(brokers, sleep, async) = args

    // Zookeeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("producer.type", async)
    props.put("batch.size", "50000")
    props.put("request.required.acks", "-1")

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Start sending
    var startTime = 0L
    var numberOfWebLogs = 0L
    var numberOfDBLogs = 0L
    while (true) {
      val webLogs = getWebLogs(WEB_LOGS_TOPIC)
      val dbLogs = getDBLogs(DB_LOGS_TOPIC)
      producer.send(webLogs: _*)
      producer.send(dbLogs: _*)
      numberOfWebLogs = numberOfWebLogs + webLogs.size
      numberOfDBLogs = numberOfDBLogs + dbLogs.size

      // Show some stats
      if (System.currentTimeMillis() - startTime > 1000) {
        startTime = System.currentTimeMillis()
        println(numberOfWebLogs + " web logs messages/s")
        println(numberOfDBLogs + " db logs messages/s")
        numberOfWebLogs = 0
        numberOfDBLogs = 0
      }
      Thread.sleep(sleep.toInt)
    }

  }

  /**
   * Create a list of web app logs
   * @param topic the topic to send messages to
   * @return the list of web app logs
   */
  private def getWebLogs(topic: String): Seq[KeyedMessage[String, String]] = {
    val producerDataList = new ArrayBuffer[KeyedMessage[String, String]]
    for (i <- 0 until 500) {
      producerDataList.append(new KeyedMessage[String, String](topic, WebAppLogGenerator.createWebServerLog()))
    }
    producerDataList
  }


  /**
   * Create a list of db logs
   * @param topic the topic to send messages to
   * @return the list of db logs
   */
  private def getDBLogs(topic: String): Seq[KeyedMessage[String, String]] = {
    val logs = new ArrayBuffer[KeyedMessage[String, String]]
    for (i <- 0 until 500) {
      val dbLog = DBLogGenerator.getDBServerLog()
      logs.append(new KeyedMessage[String, String](topic, dbLog._1))
      logs.append(new KeyedMessage[String, String](topic, dbLog._2))
    }
    logs
  }


}
