package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.all._
import com.example.infrastructure.server.middleware.common.ErrorDto
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.headers.Connection
import org.http4s.syntax.string._
import org.typelevel.log4cats.Logger
import com.example.infrastructure.server.middleware.common.ErrorCodecs._

import scala.util.control.NonFatal

private[server] object ServerErrorsAsJson {

  def apply[F[_]: Concurrent: Timer](httpRoutes: HttpApp[F])(implicit logger: Logger[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      val response = Response[F](Status.InternalServerError)
        .withHeaders(Connection("close".ci))
        .withEntity(responseEntity(request.pathInfo).asJson)
        .pure[F]

      httpRoutes.run(request).handleErrorWith {
        case NonFatal(ex) =>
          logger.error(ex)(s"Internal server error: reason=${ex.getMessage}")
          response
      }
    }

  private def responseEntity(path: String): ErrorDto =
    ErrorDto(
      code = "InternalServerError",
      message = "Internal server error",
      path = path
    )

}
