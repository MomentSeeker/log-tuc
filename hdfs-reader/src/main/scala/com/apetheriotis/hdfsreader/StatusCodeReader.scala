package com.apetheriotis.hdfsreader

import com.mongodb.casbah.Imports._
import org.apache.hadoop.fs.Path
import java.io.{InputStreamReader, BufferedReader}

object StatusCodeReader extends AbstractHdfsReader {

  var STATUS_CODE_PATH = "status_codes"
  var COLLECTION_NAME = "statusCodes"

  /**
   * Read data from hdfs related to status code.
   * Save aggregated data to mongoDB.
   * Delete read files from hdfs
   */
  def updateStatusCodeData() {

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

    println("Merging Status Codes for: " + time )

    // Setup entry
    val entry = MongoDBObject("time" -> time)
    var statusCodeAggregated: Map[String, Long] = Map(
      "status_200" -> 0,
      "status_503" -> 0,
      "status_401" -> 0,
      "status_404" -> 0,
      "status_500" -> 0,
      "status_403" -> 0)

    // Foreach file in the job
    files foreach {
      filePath =>
        val br = new BufferedReader(new InputStreamReader(fileSystem.open(new Path(filePath))))
        var line = br.readLine()
        while (line != null) {
          val data = line.split(",")
          // Deserialize data
          val statusCode = data(0).replace("(", "")
          val times = data(1).replace(",", "").replace(")", "").toLong
          statusCodeAggregated += ("status_" + statusCode -> times)
          line = br.readLine()
        }
    }

    // Save to datastore
    entry.put("isRead", false)
    entry.put("statusCodes", statusCodeAggregated)
    coll.getCollection("statusCodes").insert(entry)
  }


}



