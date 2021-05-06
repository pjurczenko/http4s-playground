package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.applicative._
import com.example.infrastructure.server.middleware.common.ErrorDto
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.server.middleware.Timeout
import org.http4s.{HttpApp, Request, Response, Status}
import com.example.infrastructure.server.middleware.common.ErrorCodecs._

import scala.concurrent.duration.FiniteDuration

private[server] object TimeoutAsJson {

  def apply[F[_]: Concurrent: Timer](
    duration: FiniteDuration
  )(httpRoutes: HttpApp[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      val response = Response[F](Status.ServiceUnavailable)
        .withEntity(responseEntity(request.pathInfo).asJson)
        .pure[F]

      Timeout(duration, response)(httpRoutes).run(request)
    }

  private def responseEntity(path: String): ErrorDto =
    ErrorDto(
      code = "RequestTimeout",
      message = "Request timed out",
      path = path
    )

}
