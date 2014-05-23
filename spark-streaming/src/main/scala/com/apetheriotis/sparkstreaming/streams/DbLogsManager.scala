package com.apetheriotis.sparkstreaming.streams

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.apetheriotis.sparkstreaming.db.RedisStorage
import scala.collection.immutable.HashSet

class DbLogsManager {

  val WEB_SERVERS_PER_DB_SERVER = 4

  def countDBQueries(stream: DStream[String]) {

    // Filter only web-server and db-server name
    val dbLogs = stream.map(log => {
      log.split("___")(3) + "," + log.split("___")(2)
    })
    val dbWebServerLogsPairs = dbLogs.map(statusCode => (statusCode.split(",")(0),
      statusCode.split(",")(1))) // db -> web-server
    val dbWebServersLogs = dbWebServerLogsPairs.groupByKeyAndWindow(Seconds(2), Seconds(2))

    // Reduce more and save to redis
    dbWebServersLogs.foreachRDD((rdd, time) => {
      val rddData = rdd.collect()
      rddData.foreach(dbServer => {
        val (dbServerName, webServersNames) = dbServer
        val set = HashSet() ++ webServersNames
        // Save all connections to db server
        RedisStorage.stringStore.put(StringToChannelBuffer("db_latest_" + dbServerName),
          Some(StringToChannelBuffer(set.mkString(","))))
        // Find any non related servers
        println(dbServerName)
        val dbServerId = dbServerName.split("-")(2).toLong
        set.foreach(webServerName => {
          val webServerId = webServerName.split("-")(2).toLong
          if (webServerId > dbServerId * 4 || webServerId <= ((dbServerId * 4)-4)) {
            RedisStorage.stringStore.put(StringToChannelBuffer("db_errors_" + dbServerName),
              Some(StringToChannelBuffer(webServerName)))
          }
        })
      })
    })

  }
}
