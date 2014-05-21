package com.apetheriotis.hdfsreader

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import scala.collection.mutable.ListBuffer
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory


trait AbstractHdfsReader {

  // Configure mongoDB
  val envConf = ConfigFactory.load()
  val mongoClient = MongoClient(envConf.getString("mongoIP"), 27017)
  val coll = mongoClient("LogTUC")

  // Configure hdfs params
  val hdfsConf = new Configuration()
  hdfsConf.addResource(new Path(envConf.getString("hadoopPath") + "/etc/hadoop/core-site.xml"))
  hdfsConf.addResource(new Path(envConf.getString("hadoopPath") + "/etc/hadoop/hdfs-site.xml"))

  // Configure filesystem
  val fileSystem = FileSystem.get(hdfsConf)

  /**
   * Lists all files for a specific job
   * @param path the file path to list files. File path should correspond to a folder with no subfolders
   * @return a tuple where
   *         first element indicates if job is completed
   *         second element refers to the time of the job
   *         third element holds the files for the job
   */
  private def listFilesForJob(path: String): (Boolean, Long, List[String]) = {

    // The time the job run
    val timeJobRun = path.toString.substring(path.indexOf("job-") + 4).toLong
    // Indicates whether job is completed or still writes data
    var isComplete = true
    val files = fileSystem.listFiles(new Path(path), true)

    // if no files in folder
    if (!files.hasNext) {
      isComplete = false
    }

    // Iterate over the files in folder and check if folder is complete, using '_SUCCESS' file
    var inFolderFiles = new ListBuffer[String]()
    while (files.hasNext) {
      val fileStatus = files.next()
      val filePath = fileStatus.getPath.toString
      if (filePath.contains("_temporary")) isComplete = false
      inFolderFiles.+=(filePath)
    }
    (isComplete, timeJobRun, inFolderFiles.toList)
  }

  /**
   * Lists all files per job, for each completed job
   * @param path the path for which to list all completed job (the job's files)
   * @return a tuple where first entry holds parent folders of jobs and second entry holds a Map where
   *         the key refers to the time of the job and the value holds the job's files
   */
  def listCompletedJobs(path: String): (Set[String], Map[Long, List[String]]) = {

    // For each hour hold all files
    var perJobFiles: Map[Long, List[String]] = Map()

    // Get parent folder for each file
    var parentFolders = Set[String]()

    try {
      val files = fileSystem.listFiles(new Path(path), true)

      while (files.hasNext) {
        parentFolders.+=(files.next().getPath.getParent.toString)
      }

      // Foreach parent folder get all files if completed
      for (parentFolder <- parentFolders) {
        val rs = listFilesForJob(parentFolder)
        if (rs._1) perJobFiles += (rs._2 -> rs._3)
      }
      (parentFolders, perJobFiles)

    } catch {
      case e: Exception =>
        (parentFolders, perJobFiles)
    }
  }

  /**
   * Delete folder from hdfs
   * @param file the file to get parent from and delete parent
   */
  def deleteFolder(file: String) {
    try {
      fileSystem.delete(new Path(file).getParent, true)
    } catch {
      case e: Exception =>
    }
  }


}

