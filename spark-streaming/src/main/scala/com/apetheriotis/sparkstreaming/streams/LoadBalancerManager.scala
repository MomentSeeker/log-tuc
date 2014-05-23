package com.apetheriotis.sparkstreaming.streams

import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.Seconds
import scala.util.Try
import com.apetheriotis.sparkstreaming.db.RedisStorage
import com.twitter.finagle.redis.util.StringToChannelBuffer

class LoadBalancerManager {

  /**
   * Counts instance ids as captured from stream every 2 seconds for the last 2 seconds and save average etc...
   * @param stream the DStream to read from
   */
  def countRequestsPerServer(stream: DStream[String]) {
    val instanceIds = stream.map(_.split("___")(7)) // 7 is the instance id
    val instanceIdsCounts = instanceIds.countByValueAndWindow(Seconds(2), Seconds(2), 3) // TODO check that 3 here
    instanceIdsCounts.print()

    // Zero all values. Big bakalia
    var totalIds = 0L
    var totalRequests = 0L
    var average = 0L
    instanceIdsCounts.foreachRDD(rdd => {
      rdd.collect()
      totalIds = 0L
      totalRequests = 0L
      average = 1L
    })

    // Compute average and save requests per instance to database
    instanceIdsCounts.foreachRDD((rdd, time) => {
      val instanceIdsInRDD = rdd.collect()
      instanceIdsInRDD.foreach(id => {
        totalIds = totalIds + 1
        totalRequests = totalRequests + id._2
      })
      average = Try(totalRequests / totalIds).getOrElse(0L)
    })

    // Find outsiders according to the threshold and save to db
    instanceIdsCounts.foreachRDD((rdd, time) => {
      val instanceIdsInRDD = rdd.collect()
      instanceIdsInRDD.foreach(id => {
        val deviation = ((id._2 * 100) / average) - 100
        var outsider = false
        if (deviation > 20 || deviation < -20) outsider = true
        RedisStorage.stringStore.put(StringToChannelBuffer("ld_interval_" + time.milliseconds + "_instance_" + id._1),
          Some(StringToChannelBuffer((id._2 + "," + deviation + "," + outsider))))
        RedisStorage.stringStore.put(StringToChannelBuffer("ld_latest_instance_" + id._1),
          Some(StringToChannelBuffer((id._2 + "," + deviation + "," + outsider))))
      })
    })

  }

}
