package com.apetheriotis.streaming.logtuc.restapi.domain

import com.apetheriotis.streaming.logtuc.restapi.dto.StatusCodeEstimationsDto
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Holds estimations for a specific time for status code counts
 */
case class StatusCodesEstimations(oid: Option[String],
                                  time: Long,
                                  statusCodes: Map[String, Long]
                                   ) extends DaoObject {

  def this() {
    this(None, 0L, Map())
  }

  /**
   * Constructor with a [[com.apetheriotis.streaming.logtuc.restapi.domain.StatusCodesEstimations]]
   */
  def this(data: StatusCodeEstimationsDto) {
    this(
      None,
      time = data.time,
      statusCodes = data.statusCodes
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
      obj.getAs[Map[String, Long]]("statusCodes").get
    )
  }

  /**
   * @return a representation of this object as Db Object
   */
  override def asDbObject(): Imports.DBObject = {
    val builder = MongoDBObject.newBuilder
    if (oid != None) builder += ("_id" -> oid)
    builder += ("time" -> time)
    builder += ("statusCodes" -> statusCodes)
    builder.result()
  }


}
