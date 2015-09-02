package com.apetheriotis.streaming.logtuc.restapi.dto

/**
 * DTO that holds estimations for status codes for a specific time
 * @param time the time the estimations have been calculated for
 * @param statusCodes the Map with the status code and the estimated count
 */
case class StatusCodeEstimationsDto(time: Long, statusCodes: Map[String, Long])
