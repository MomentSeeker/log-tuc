package com.apetheriotis.sparkstreaming.db

import com.twitter.finagle.redis.Client
import com.twitter.storehaus.redis.{RedisStore, RedisLongStore}

object RedisStorage {

   val redisClient = Client("localhost:6379")
  val store =  RedisLongStore(redisClient)

  val stringStore =  RedisStore(redisClient)

 }
