package com.apetheriotis.logproducer

import java.util.Properties
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}


object LogProducer {

  def main(args: Array[String]) {

    // Get parameters from input
    if (args.length != 3) {
      System.err.println("Usage: brokers topic timeToSleep")
      System.exit(1)
    }
    val Array(brokers, topic, timeToSleep) = args

    // Zookeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)


    // Start sending
    while (true) {
      producer.send(new KeyedMessage[String, String](topic, LogGenerator.getLogLine()))
      Thread.sleep(timeToSleep.toLong)
    }

  }

}
