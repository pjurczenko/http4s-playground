package com.example.infrastructure.server

import cats.effect.{ConcurrentEffect, Resource, Timer}
import com.example.api.book.BookEndpoint
import com.example.infrastructure.config.AppConfig
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.example.infrastructure.server.util.Slf4jLoggerUtil.Slf4jLoggerOps

import scala.concurrent.ExecutionContext

object AppServer {

  def create[F[_]: ConcurrentEffect: Timer](
    config: AppConfig,
    bookEndpoint: BookEndpoint[F],
    executionContext: ExecutionContext
  ): Resource[F, Server[F]] =
    Slf4jLogger.createR[F].flatMap { implicit logger =>
      BlazeServerBuilder[F](executionContext)
        .bindHttp(host = config.http.server.host, port = config.http.server.port)
        .withHttpApp(AppRoutes.create(bookEndpoint, config.http.server.timeout))
        .withMaxConnections(config.http.server.maxConnections)
        .resource
    }

}
