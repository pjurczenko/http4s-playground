package com.example.infrastructure.server

import cats.effect.{ConcurrentEffect, Timer}
import com.example.api.book.BookEndpoint
import com.example.infrastructure.server.middleware.{AppErrorsAsJson, ClientErrorsAsJson, NotFoundAsJson, ServerErrorsAsJson, StreamAwareRequestLogger, StreamAwareResponseLogger, TimeoutAsJson}
import org.http4s.HttpApp
import org.http4s.server.middleware.AutoSlash
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.FiniteDuration

object AppRoutes {

  def create[F[_]: ConcurrentEffect: Timer](
    bookEndpoint: BookEndpoint[F],
    requestTimeout: FiniteDuration
  )(implicit logger: Logger[F]): HttpApp[F] =
    StreamAwareResponseLogger {
      TimeoutAsJson(requestTimeout) {
        ServerErrorsAsJson {
          AppErrorsAsJson {
            ClientErrorsAsJson {
              StreamAwareRequestLogger {
                NotFoundAsJson {
                  AutoSlash {
                    bookEndpoint.routes
                  }
                }
              }
            }
          }
        }
      }
    }

}
