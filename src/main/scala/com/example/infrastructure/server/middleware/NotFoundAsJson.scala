package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.applicative._
import com.example.infrastructure.server.middleware.common.ErrorDto
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{HttpApp, Request, Response, Status, _}
import com.example.infrastructure.server.middleware.common.ErrorCodecs._

private[server] object NotFoundAsJson {

  def apply[F[_]: Concurrent: Timer](httpRoutes: HttpRoutes[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      val response = Response[F](Status.NotFound)
        .withEntity(responseEntity(request.pathInfo).asJson)
        .pure[F]

      httpRoutes.run(request).getOrElseF(response)
    }

  def responseEntity(path: String): ErrorDto =
    ErrorDto(
      code = "PathNotFound",
      message = "Path not found",
      path = path
    )

}
