package com.apetheriotis.logproducer

import java.util.Properties
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.ArrayBuffer

/**
 * Creates web and db like logs and sends them to kafka
 */
object LogProducer {

  val envConf = ConfigFactory.load()

  def main(args: Array[String]) {

    // Print sample logs
    println(WebAppLogGenerator.createWebServerLog())
    println(DBLogGenerator.getDBServerLog())

    // Get parameters from input
    if (args.length != 1) {
      System.err.println("Usage: timeToSleep")
      System.exit(1)
    }
    val Array(timeToSleep) = args

    // Zookeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", envConf.getString("brokers"))
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("producer.type", "async");
    props.put("queue.enqueue.timeout.ms", "-1");
    props.put("batch.num.messages", "500");

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Start sending
    var startTime = 0L;
    var numberOfWebLogs = 0L;
    var numberOfDBLogs = 0L;
    while (true) {
      val webLogs = getWebLogs(envConf.getString("webLogsTopic"))
      val dbLogs = getDBLogs(envConf.getString("dbLogsTopic"))
      producer.send(webLogs: _*)
      producer.send(dbLogs: _*)
      numberOfWebLogs = numberOfWebLogs + webLogs.size
      numberOfDBLogs = numberOfDBLogs + dbLogs.size
      if (System.currentTimeMillis() - startTime > 1000) {
        startTime = System.currentTimeMillis()
        println(numberOfWebLogs + " web logs messages/s")
        println(numberOfDBLogs + " db logs messages/s")
        numberOfWebLogs = 0
        numberOfDBLogs = 0
      }
      if (timeToSleep.toLong != 0) Thread.sleep(timeToSleep.toLong)
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
