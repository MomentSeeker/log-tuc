package com.apetheriotis.sparkstreaming.streams

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.apetheriotis.sparkstreaming.db.RedisStorage


class HttpStatusCodeManager {

  /**
   * Counts status codes as captured from stream every 2 seconds for the last 2 seconds and saves results to redis
   * @param stream the DStream to read from
   */
  def countStatusCodes(stream: DStream[String]) {

    val statusCodes = stream.map(_.split("___")(2)) // 2 is the status code
    val pairs = statusCodes.map(statusCode => (statusCode, 1))
    val statusCodesCounts = pairs.reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2), 3) // TODO check that 3 here
    statusCodesCounts.print()

    statusCodesCounts.foreachRDD((rdd, time) => {
      RedisStorage.store.put(StringToChannelBuffer("last_rdd_time"), Some(time.milliseconds))
      val rddData = rdd.collect()
      rddData.foreach(statusCodesCount => {
        val (key, value) = statusCodesCount
        RedisStorage.store.merge(StringToChannelBuffer("sc_total_" + key), value)
        RedisStorage.store.put(StringToChannelBuffer("sc_latest_" + key), Some(value))
        RedisStorage.store.put(StringToChannelBuffer("sc_interval_" + time.milliseconds + "_" + key), Some(value))
      })
    })
  }

}
