package com.apetheriotis.hdfsreader

import com.mongodb.casbah.Imports._
import org.apache.hadoop.fs.Path
import java.io.{InputStreamReader, BufferedReader}

object InstanceIdReader extends AbstractHdfsReader {

  var STATUS_CODE_PATH = "instance_ids"
  var COLLECTION_NAME = "instanceIds"

  /**
   * Read data from hdfs related to instance ids.
   * Save aggregated data to mongoDB.
   * Delete read files from hdfs
   */
  def updateInstanceIdData() {

    // Setup full path and get files for each completed job
    val path = envConf.getString("hdfsURL") + STATUS_CODE_PATH
    val filesPerJob = listCompletedJobs(path)

    // Merge all files for each job and save to mongoDB
    filesPerJob._2.foreach(x => mergeParts(x._1, x._2))

    // Delete folder
    filesPerJob._1.foreach(filePath => deleteFolder(filePath))
  }


  /**
   * Merge all files for the job and save to mongoDB
   * @param time the time of the job
   * @param files the files associated to the job
   */
  private def mergeParts(time: Long, files: List[String]) {

    println("Merging Instance Ids for: " + time )

    // Setup entry
    val entry = MongoDBObject("time" -> time)
    var instanceIdsAggregated: Map[String, Long] = Map()

    // Foreach file in the job
    files foreach {
      filePath =>
        val br = new BufferedReader(new InputStreamReader(fileSystem.open(new Path(filePath))))
        var line = br.readLine()
        while (line != null) {
          val data = line.split(",")
          // Deserialize data
          val instanceId = data(0).replace("(", "")
          val times = data(1).replace(",", "").replace(")", "").toLong
          instanceIdsAggregated += (instanceId -> times)
          line = br.readLine()
        }
    }

    // Save to datastore
    entry.put("isRead", false)
    entry.put("instanceIds", instanceIdsAggregated)
    coll.getCollection(COLLECTION_NAME).insert(entry)
  }


}



