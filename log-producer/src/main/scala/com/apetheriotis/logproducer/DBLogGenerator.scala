package com.apetheriotis.logproducer

import scala.collection.mutable.ListBuffer
import scala.util.Random
import java.util.Date

/**
 * Constructs mongo db like logs. Each db-server accepts connections from 4 web-servers at most.
 */
object DBLogGenerator {

  // Generate sample db server names
  var dbServersNamesTemp = new ListBuffer[String]()
  for (i <- 1 to 25) dbServersNamesTemp += "db-server-" + i
  val dbServersNames = dbServersNamesTemp.toList

  // Generate sample web server names
  var webServerNamesTemp = new ListBuffer[String]()
  for (i <- 1 to 100) webServerNamesTemp += "web-server-" + i
  val webServerNames = webServerNamesTemp.toList

  /**
   * Queries for a random web server and a db server name
   * @return the instance id
   */
  private def getServersNames(): (String, String) = {
    val dbServerId = Random.nextInt(25)
    val webServerId = Random.nextInt(4) + dbServerId * 4
    (dbServersNames(dbServerId), webServerNames(webServerId))
  }


  /**
   * Creates db logs. One for start connection and one for end connection
   * @return a tuple where first entry is the start connection log and the second entry corresponds to end connection
   */
  def getDBServerLog(): (String, String) = {
    val serverNames = getServersNames()
    var time = new Date()
    // time___initMessage___clientName#hostname
    val startMessage = time.toString + "___[initandlisten] connection accepted from___" + serverNames._2 + "#" + serverNames._1
    // time___stopMessage___clientName#hostname
    time.setTime(time.getTime + new Random().nextInt(50))
    val stopMessage = time.toString + "___[initandlisten] end connection from___" + serverNames._2 + "#" + serverNames._1
    (startMessage, stopMessage)
  }

}
