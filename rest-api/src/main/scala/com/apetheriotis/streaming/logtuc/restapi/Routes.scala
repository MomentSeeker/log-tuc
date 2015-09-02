package com.apetheriotis.streaming.logtuc.restapi

import com.apetheriotis.streaming.logtuc.restapi.dao.{RequestsPerServerEstimationsDao, StatusCodesEstimationsDao}
import com.apetheriotis.streaming.logtuc.restapi.domain.{RequestsPerServerEstimations, StatusCodesEstimations}
import com.apetheriotis.streaming.logtuc.restapi.dto.{RequestsPerServerEstimationsDto, StatusCodeEstimationsDto}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService

/**
 * Rest API routes
 */
trait Routes extends HttpService with SprayJsonSupport with DefaultJsonProtocol {

  implicit val statusCodeEstimationsDto = jsonFormat2(StatusCodeEstimationsDto)
  implicit val requestsPerServerEstimationsDto = jsonFormat2(RequestsPerServerEstimationsDto)


  /* Routes to GET, POST status code estimations */
  val statusCodeRoutes =
  // --- GET latest codes estimations --- //
    pathPrefix("api" / "v1" / "status_codes_estimations" / "latest") {
      get { ctx =>
        val latest = StatusCodesEstimationsDao.getLatestEstimations
        ctx.complete(new StatusCodeEstimationsDto(latest.time, latest.statusCodes))
      }
    } ~ // --- GET aggregated codes estimations --- //
      pathPrefix("api" / "v1" / "status_codes_estimations" / "aggregated") {
        get { ctx =>
          val total = StatusCodesEstimationsDao.getTotalEstimations
          ctx.complete(new StatusCodeEstimationsDto(total.time, total.statusCodes))
        }
        // --- POST new status codes estimations --- //
      } ~ pathPrefix("api" / "v1" / "status_codes_estimations") {
      post {
        entity(as[StatusCodeEstimationsDto]) { data =>
          val estimatedSC = new StatusCodesEstimations(data)
          StatusCodesEstimationsDao.save(estimatedSC)
          complete("ok")
        }
      }
    }

  /* Routes to GET, POST requests per server estimations */
  val requestsPerServerRoutes =
  // --- GET latest requests per server estimations --- //
    pathPrefix("api" / "v1" / "requests_per_server_estimations" / "latest") {
      get { ctx =>
        val latest = RequestsPerServerEstimationsDao.getLatestEstimations
        ctx.complete(new RequestsPerServerEstimationsDto(latest.time, latest.requestsPerServer))
      }
      // --- POST new requests per server estimations --- //
    } ~ pathPrefix("api" / "v1" / "requests_per_server_estimations") {
      post {
        entity(as[RequestsPerServerEstimationsDto]) { data =>
          val estimated = new RequestsPerServerEstimations(data)
          RequestsPerServerEstimationsDao.save(estimated)
          complete("ok")
        }
      }
    }

  /* Static pages routes to retrieve html and js files */
  val staticPagesRoutes =
    pathPrefix("static" / Segment) {
      html =>
        get {
          getFromResource("static/" + html)
        }
    } ~ pathPrefix("static" / Segment / Segment) {
      (folder, html) =>
        get {
          getFromResource("static/" + folder + "/" + html)
        }
    }


}
