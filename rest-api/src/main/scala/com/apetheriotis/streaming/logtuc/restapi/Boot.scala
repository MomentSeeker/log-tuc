package com.apetheriotis.streaming.logtuc.restapi


import akka.actor.{Props, ActorSystem}
import spray.can.Http
import akka.io.IO

object Boot extends App {
  implicit val system = ActorSystem("LogTucActorSystem")
  val service = system.actorOf(Props[LogTucServiceActor], "apiService")
  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = 9098)
}