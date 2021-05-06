package com.example.server.util

import cats.effect.IO
import org.http4s.{HttpApp, Request, Response}

object HttpAppUtil {
  implicit class HttpAppOps(private val self: HttpApp[IO]) extends AnyVal {
    def execute(request: Request[IO]): Response[IO] = self.run(request).unsafeRunSync()
  }
}
