package com.apetheriotis.streaming.logtuc.restapi.dao

import com.apetheriotis.streaming.logtuc.restapi.domain.StatusCodesEstimations
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Dao for [[com.apetheriotis.streaming.logtuc.restapi.domain.StatusCodesEstimations]]
 */
object StatusCodesEstimationsDao extends CommonDao[StatusCodesEstimations] {
  val COLLECTION_NAME = "statusCodesEstimations"

  override def getCollectionName: String = COLLECTION_NAME

  /**
   * Query for latest estimations for Status Codes counts
   */
  def getLatestEstimations: StatusCodesEstimations = {
    val orderQ = MongoDBObject("time" -> -1)
    val data = db(getCollectionName).find().sort(orderQ).limit(1)
    new StatusCodesEstimations(data.one())
  }

  /**
   * Sum all estimations into one total estimation
   */
  def getTotalEstimations: StatusCodesEstimations = {
    import scalaz.Scalaz._
    val data = listAll()
    val rs = if (data.isEmpty) List() else data.map(x => new StatusCodesEstimations(x)).toList.map(x => x.statusCodes)
    val totalEstimations = rs.reduce(_ |+| _)
    new StatusCodesEstimations(None, 0L, totalEstimations)
  }


}