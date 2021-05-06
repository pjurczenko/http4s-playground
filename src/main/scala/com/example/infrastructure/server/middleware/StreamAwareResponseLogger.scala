package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.flatMap._
import org.http4s.server.middleware.{Logger => Http4sLogger}
import org.http4s.{HttpApp, Request, Response}
import org.typelevel.log4cats.Logger

private[server] object StreamAwareResponseLogger {

  def apply[F[_]: Concurrent: Timer](httpApp: HttpApp[F])(implicit logger: Logger[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      httpApp.run(request).flatTap { response =>
        Http4sLogger
          .logMessage[F, Response[F]](response)(logHeaders = true, logBody = !response.isChunked)(logger.info(_))
      }
    }

}
