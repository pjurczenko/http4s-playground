package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.applicativeError._
import com.example.common.AppError
import com.example.infrastructure.server.middleware.common.ErrorDto
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, Request}
import com.example.common.BookModuleError.{BookNotFound, ValidationFailure}
import com.example.infrastructure.server.middleware.common.ErrorCodecs._

private[server] object AppErrorsAsJson {

  def apply[F[_]: Concurrent: Timer](httpRoutes: HttpApp[F]): HttpApp[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    Kleisli { (request: Request[F]) =>
      httpRoutes.run(request).recoverWith {
        case error: AppError =>
          error match {
            case BookNotFound(_) =>
              NotFound(bookNotFound(request.pathInfo).asJson)
            case ValidationFailure(reason) =>
              BadRequest(validationFailure(reason, request.pathInfo).asJson)
          }
      }
    }
  }

  private def bookNotFound(path: String): ErrorDto =
    ErrorDto(
      code = "BookNotFound",
      message = s"Book not found",
      path = path
    )

  private def validationFailure(reason: String, path: String): ErrorDto =
    ErrorDto(
      code = "ValidationFailure",
      message = reason.capitalize,
      path = path
    )

}
