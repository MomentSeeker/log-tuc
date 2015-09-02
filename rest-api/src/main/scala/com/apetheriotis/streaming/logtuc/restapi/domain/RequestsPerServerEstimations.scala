package com.apetheriotis.streaming.logtuc.restapi.domain

import com.apetheriotis.streaming.logtuc.restapi.dto.RequestsPerServerEstimationsDto
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Holds estimations for a specific time for the count of requests to a specific server
 */
case class RequestsPerServerEstimations(oid: Option[String],
                                       time: Long,
                                       requestsPerServer: Map[String, Long]
                                        ) extends DaoObject {

  def this() {
    this(None, 0L, Map())
  }

  /**
   * Constructor with a [[com.apetheriotis.streaming.logtuc.restapi.dto.RequestsPerServerEstimationsDto]]
   */
  def this(data: RequestsPerServerEstimationsDto) {
    this(
      None,
      time = data.time,
      requestsPerServer = data.requestsPerServer
    )
  }

  /**
   * Constructor with a DBObject
   * @param obj the DBObject from which to retrieve data
   */
  def this(obj: Imports.DBObject) {
    this(
      Some(obj.get("_id").toString),
      obj.getAs[Long]("time").get,
      obj.getAs[Map[String, Long]]("requestsPerServer").get
    )
  }

  /**
   * @return a representation of this object as Db Object
   */
  override def asDbObject(): Imports.DBObject = {
    val builder = MongoDBObject.newBuilder
    if (oid != None) builder += ("_id" -> oid)
    builder += ("time" -> time)
    builder += ("requestsPerServer" -> requestsPerServer)
    builder.result()
  }


}
