package com.example.infrastructure.server.middleware

import cats.data.Kleisli
import cats.effect.{Concurrent, Timer}
import cats.syntax.flatMap._
import org.http4s.server.middleware.{Logger => Http4sLogger}
import org.http4s.{HttpApp, Request}
import org.typelevel.log4cats.Logger

private[server] object StreamAwareRequestLogger {

  def apply[F[_]: Concurrent: Timer](httpApp: HttpApp[F])(implicit logger: Logger[F]): HttpApp[F] =
    Kleisli { (request: Request[F]) =>
      Http4sLogger
        .logMessage[F, Request[F]](request)(logHeaders = true, logBody = !request.isChunked)(logger.info(_))
        .flatMap(_ => httpApp.run(request))
    }

}
