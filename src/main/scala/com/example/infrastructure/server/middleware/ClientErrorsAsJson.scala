package com.example.infrastructure.server.middleware

import cats.Applicative
import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import com.example.infrastructure.server.middleware.common.ErrorDto
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import com.example.infrastructure.server.middleware.common.ErrorCodecs._

private[server] object ClientErrorsAsJson {

  // TODO: provide details about JSON failures
  def apply[F[_]: Concurrent: Timer](httpRoutes: HttpApp[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      httpRoutes.run(request).recoverWith {
        case messageFailure: MessageFailure =>
          messageFailure match {
            case _: InvalidMessageBodyFailure =>
              response(Status.BadRequest, invalidMessageBody(request.pathInfo))
            case _: MalformedMessageBodyFailure =>
              response(Status.BadRequest, malformedMessageBody(request.pathInfo))
            case ex: MediaTypeMismatch =>
              response(Status.UnsupportedMediaType, mediaTypeMismatch(ex.message, request.pathInfo))
            case ex: MediaTypeMissing =>
              response(Status.UnsupportedMediaType, mediaTypeMissing(ex.message, request.pathInfo))
            case ex: ParseFailure =>
              response(Status.BadRequest, parseFailure(ex.sanitized, request.pathInfo))
          }
      }
    }

  private def response[F[_]: Applicative](status: Status, error: ErrorDto): F[Response[F]] =
    Response[F](status).withEntity(error.asJson).pure[F]

  private def invalidMessageBody(path: String): ErrorDto =
    ErrorDto(
      code = "InvalidMessageBody",
      message = "The request body was invalid",
      path = path
    )

  private def malformedMessageBody(path: String): ErrorDto =
    ErrorDto(
      code = "MalformedMessageBody",
      message = "The request body was malformed",
      path = path
    )

  private def mediaTypeMismatch(message: String, path: String): ErrorDto =
    ErrorDto(
      code = "MediaTypeMismatch",
      message = message,
      path = path
    )

  private def mediaTypeMissing(message: String, path: String): ErrorDto =
    ErrorDto(
      code = "MediaTypeMissing",
      message = message,
      path = path
    )

  private def parseFailure(message: String, path: String): ErrorDto =
    ErrorDto(
      code = "ParseFailure",
      message = message,
      path = path
    )

}
