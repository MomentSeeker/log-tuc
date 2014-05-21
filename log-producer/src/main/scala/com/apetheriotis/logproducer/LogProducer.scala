package com.apetheriotis.logproducer

import java.util.Properties
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.ArrayBuffer


object LogProducer {

  val envConf = ConfigFactory.load()

  def main(args: Array[String]) {

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


    println(LogGenerator.getLogLine())

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Start sending
    var startTime = 0L;
    var numberOfMessages = 0L;
    while (true) {
      val logs = getLogs(envConf.getString("topic"))
      producer.send(logs: _*)
      numberOfMessages = numberOfMessages + logs.size
      if (System.currentTimeMillis() - startTime > 1000) {
        startTime = System.currentTimeMillis()
        println(numberOfMessages + " messages/s")
        numberOfMessages = 0
      }

      if (timeToSleep.toLong != 0) {
        Thread.sleep(timeToSleep.toLong)
      }

    }

  }

  private def getLogs(topic: String): Seq[KeyedMessage[String, String]] = {
    val producerDataList = new ArrayBuffer[KeyedMessage[String, String]]
    for (i <- 0 until 500) {
      producerDataList.append(new KeyedMessage[String, String](topic, LogGenerator.getLogLine()))
    }
    producerDataList
  }

}
