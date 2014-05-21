package com.apetheriotis.hdfsreader


object MainHdfsReader {

  def main(args: Array[String]) {

    while (true) {
      readStatus()
      Thread.sleep(2000)
    }
  }


  def readStatus() {
    try {
      StatusCodeReader.updateStatusCodeData()
      InstanceIdReader.updateInstanceIdData()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }


  }


}

