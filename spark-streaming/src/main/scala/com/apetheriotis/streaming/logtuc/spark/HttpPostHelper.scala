package com.apetheriotis.streaming.logtuc.spark

import java.util.Date

import com.google.gson.Gson

import scala.collection.JavaConversions._
import scalaj.http.{Http, HttpOptions}

/**
 * Helper class that sends CMS estimations to a rest API
 *
 * @author Angelos Petheriotis
 */
object HttpPostHelper {

  val STATUS_CODES_SERVER_ENDPOINT = "http://localhost:9098/api/v1/status_codes_estimations"
  val REQUESTS_PER_SERVER_SERVER_ENDPOINT = "http://localhost:9098/api/v1/requests_per_server_estimations"

  /**
   * Send status code estimations to specified server
   */
  def sendStatusCodesEstimations(estimations: Seq[(Long, Long)]): Unit = {
    val time = new Date().getTime // here we can get the rdd time but for simplicity we use this
    val estimatedRs = new Gson().toJson(mapAsJavaMap(estimations.map(x => "status_" + x._1 -> x._2).toMap)) // add 'status_' before the code
    val payload = s"""{"time":$time,"statusCodes":$estimatedRs}"""
    try {
      Http(STATUS_CODES_SERVER_ENDPOINT).header("Content-Type", "application/json").header("Charset", "UTF-8")
        .method("POST").postData(payload)
        .option(HttpOptions.readTimeout(10000)).asString
    } catch {
      case e: Exception => println("failed..")
    }
  }

  /**
   * Send estimations for requests/server
   */
  def sendRequestsPerServerEstimations(estimations: Seq[(Long, Long)]): Unit = {
    val time = new Date().getTime // here we can get the rdd time but for simplicity we use this
    val estimatedRs = new Gson().toJson(mapAsJavaMap(estimations.map(x =>  x._1 -> x._2).toMap))
    val payload = s"""{"time":$time,"requestsPerServer":$estimatedRs}"""
    try {
      Http(REQUESTS_PER_SERVER_SERVER_ENDPOINT).header("Content-Type", "application/json").header("Charset", "UTF-8")
        .method("POST").postData(payload)
        .option(HttpOptions.readTimeout(10000)).asString
    } catch {
      case e: Exception => println("failed..")
    }
  }

}
