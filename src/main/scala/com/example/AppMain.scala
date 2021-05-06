package com.example

import cats.effect.{Async, Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.example.api.book.BookEndpoint
import com.example.domain.book.BookModule
import com.example.infrastructure.config.AppConfig
import com.example.infrastructure.server.AppServer

object AppMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    runF[IO]

  private def runF[F[_]: ConcurrentEffect: ContextShift: Timer]: F[ExitCode] = {
    val server = for {
      blocker <- Blocker[F]
      config <- Resource.liftF(AppConfig.load(blocker))
      bookModule <- Resource.liftF(BookModule.create)
      bookEndpoint <- Resource.liftF(BookEndpoint.create(bookModule))
      server <- AppServer.create(config, bookEndpoint, executionContext)
    } yield server

    server.use(_ => Async[F].never)
  }

}
