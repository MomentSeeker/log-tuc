package com.apetheriotis.streaming.logtuc.restapi.dao

import com.apetheriotis.streaming.logtuc.restapi.domain.RequestsPerServerEstimations
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Dao for [[com.apetheriotis.streaming.logtuc.restapi.domain.RequestsPerServerEstimations]]
 */
object RequestsPerServerEstimationsDao extends CommonDao[RequestsPerServerEstimations] {
  val COLLECTION_NAME = "requestsPerServerEstimations"

  override def getCollectionName: String = COLLECTION_NAME

  /**
   * Query for latest estimations for Status Codes counts
   */
  def getLatestEstimations: RequestsPerServerEstimations = {
    val orderQ = MongoDBObject("time" -> -1)
    val data = db(getCollectionName).find().sort(orderQ).limit(1)
    new RequestsPerServerEstimations(data.one())
  }


}