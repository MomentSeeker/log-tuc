package com.apetheriotis


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path
import java.io.{InputStreamReader, BufferedReader}
import scala.collection.mutable.ListBuffer;
import com.mongodb.casbah.Imports._


object MyRouteMain {

  val conf = new Configuration();
  conf.addResource(new Path("/home/agg3l0st/Programs/hadoop-2.2.0/etc/hadoop/core-site.xml"));
  conf.addResource(new Path("/home/agg3l0st/Programs/hadoop-2.2.0/etc/hadoop/hdfs-site.xml"));


  val JOB_NAME = "data"
  val JOB_ID = "file.txt"

  def main(args: Array[String]) {


    var toDeleteFolders = new ListBuffer[String]()
    val fileSystem = FileSystem.get(conf)


    // Forever check hdfs
    while (true) {

      // Get files
      val files = fileSystem.listFiles(new Path("hdfs://localhost:8020/" + JOB_NAME), true)
      while (files.hasNext) {
        val file = files.next()

        // Get parent folder of current file
        val parentFolder = file.getPath.getParent.toString

        // If we have delete that folder move on
        if (!toDeleteFolders.contains(parentFolder)) {

          // Filter job run
          val timeJobRun = parentFolder.toString.substring(parentFolder.indexOf("-") + 1)

          // Check if batch job has finished. Get all files inside a batch's job and search for a file "_SUCCESS"
          val filesInBatchJob = fileSystem.listFiles(new Path(parentFolder), true)

          var toMergeFiles = new ListBuffer[String]()
          while (filesInBatchJob.hasNext) {
            val innerFile = filesInBatchJob.next().getPath.toString
            println(innerFile)
            // If success add to be deleted
            if (innerFile.endsWith("_SUCCESS")) {
              toDeleteFolders.+=(parentFolder)
            }
            else {
              toMergeFiles.+=(innerFile)
            }
          }

          mergeAllParts(timeJobRun.toLong, toMergeFiles.toList)

        }
      }

      toDeleteFolders.toList.foreach(folder => fileSystem.delete(new Path(folder), true));
      toDeleteFolders.clear()
      println("waiting...")
      Thread.sleep(1000)
    }

  }


  def mergeAllParts(time: Long, files: List[String]) {

    val mongoClient = MongoClient("localhost", 27017)
    val coll = mongoClient("LogTUC")
    val a = MongoDBObject("time" -> time)

    //

    val map: Map[Integer, Integer] = Map.empty

    files foreach {
      filePath =>
        val fs = FileSystem.get(conf)
        val br = new BufferedReader(new InputStreamReader(fs.open(new Path(filePath))))
        var line = br.readLine()
        while (line != null) {

          val data = line.split(",")

          val statusCode = data(0).replace("(", "")
          val times = data(1).replace(",", "").replace(")", "").toLong
          println(statusCode + "-" + times + "!")

          a.put(statusCode, times)

          System.out.println(line)
          line = br.readLine()
        }

    }


    coll.getCollection("lala").insert(a)

  }


}

