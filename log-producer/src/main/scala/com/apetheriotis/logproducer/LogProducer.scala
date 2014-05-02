package com.apetheriotis.logproducer

import java.util.Properties
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import com.typesafe.config.ConfigFactory


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

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Start sending
    while (true) {
      producer.send(new KeyedMessage[String, String](envConf.getString("topic"), LogGenerator.getLogLine()))
      Thread.sleep(timeToSleep.toLong)
    }

  }

}
