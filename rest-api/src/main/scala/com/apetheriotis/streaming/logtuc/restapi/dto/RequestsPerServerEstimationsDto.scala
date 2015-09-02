package com.apetheriotis.streaming.logtuc.restapi.dto

/**
 * DTO that holds estimations for requests per server for a specific time
 * @param time the time the estimations have been calculated for
 * @param requestsPerServer the Map with the server id and the estimated count
 */
case class RequestsPerServerEstimationsDto(time: Long, requestsPerServer: Map[String, Long])
