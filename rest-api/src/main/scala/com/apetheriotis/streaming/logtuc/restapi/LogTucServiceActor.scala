package com.apetheriotis.streaming.logtuc.restapi

import akka.actor.Actor
import org.json4s.{DefaultFormats, Formats}
import spray.routing.HttpService

/**
 * An actor that runs the routes
 */
class LogTucServiceActor extends Actor with HttpService with Routes {

  implicit def json4sFormats: Formats = DefaultFormats

  def actorRefFactory = context

  def receive = runRoute(statusCodeRoutes ~ requestsPerServerRoutes ~ staticPagesRoutes)

}
