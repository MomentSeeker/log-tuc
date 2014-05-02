package com.apetheriotis.sparkstreaming

import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel
import kafka.serializer.StringDecoder


/**
 * @author Angelos Petheriotis
 */
object SparkStreamingDummyExamples {


  def main(args: Array[String]) {


    val kafkaParams = Map[String, String](
      "zookeeper.connect" -> "127.0.0.1:2181",
      "group.id" -> "LogTucConsumers",
      "zookeeper.connection.timeout.ms" -> "10000",
      "auto.commit.interval.ms" -> "10000", // when to commit to zookeeper
      "auto.offset.reset" -> "largest") // at which point to restart

    println(System.getenv("SPARK_HOME"))

    val ssc = new StreamingContext("spark://agg3l0st-Laptop:7077", "KafkaWordCount", Seconds(2),
      "/opt/spark/spark-0.9.0-incubating/", StreamingContext.jarOfClass(this.getClass))

    // Fix error "No FileSystem for scheme: hdfs" with the following:
    val hadoopConfig = ssc.sparkContext.hadoopConfiguration
    hadoopConfig.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName())
    hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName())


    ssc.checkpoint("hdfs://localhost:8020/checkpoints")

    val lines = KafkaUtils.createStream[String, String, StringDecoder,
      StringDecoder](ssc, kafkaParams, Map("LogTUC" -> 1),
        StorageLevel.MEMORY_ONLY_SER_2).map(_._2)


    // ---- Group by ip // DO NOT USE // ----
    //    val words = lines.map(_.split("___")(0))
    //    val wordCounts = words.map(x => (x, 1L)).groupByKeyAndWindow(Seconds(2), Seconds(2))
    //    wordCounts.print()

    // ---- Count by ip...no windows ---
    //    val words = lines.map(_.split("___")(0)) // 0 is the ip
    //    val pairs = words.map(word => (word, 1))
    //    val wordCounts = pairs.reduceByKey(_ + _)
    //    wordCounts.print()

    // ---- Count by ip with window ---
    //    val words = lines.map(_.split("___")(0)) // 0 is the ip
    //    val pairs = words.map(word => (word, 1))
    //    val wordCounts = pairs.reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2)).filter(x=>x._2>0)
    //    wordCounts.print()

    // ---- Multiple kafka streams ---
    //    val kafkaInputs = (1 to 10).map { _ =>
    //      KafkaUtils.createStream[String, String, StringDecoder,
    //        StringDecoder](ssc, kafkaParams, Map("LogTUC" -> 1),
    //          StorageLevel.MEMORY_AND_DISK_SER_2).map(_._2).repartition(3)
    //    }
    //    val union = ssc.union(kafkaInputs).repartition(3)


    // ---- Count by status code with window ---
    val words = lines.map(_.split("___")(2)) // 2 is the status code
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2), 3)
    wordCounts.print()

    wordCounts.saveAsTextFiles("hdfs://localhost:8020/data/file.txt")



    ssc.start()
    ssc.awaitTermination()
  }

}
