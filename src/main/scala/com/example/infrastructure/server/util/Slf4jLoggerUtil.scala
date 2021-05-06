package com.example.infrastructure.server.util

import cats.effect.{Resource, Sync}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

private[server] object Slf4jLoggerUtil {
  implicit class Slf4jLoggerOps(private val self: Slf4jLogger.type) extends AnyVal {
    def createR[F[_]: Sync]: Resource[F, SelfAwareStructuredLogger[F]] =
      Resource.liftF(self.create[F])
  }
}
