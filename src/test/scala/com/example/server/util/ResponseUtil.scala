package com.example.server.util

import cats.effect.IO
import org.http4s.Response

object ResponseUtil {
  implicit class ResponseOps(private val self: Response[IO]) extends AnyVal {
    def string: String = self.as[String].unsafeRunSync()
  }
}
