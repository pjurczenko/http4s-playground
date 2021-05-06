package com.example.api.common

import cats.MonadError
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.monadError._
import com.example.common.AppError
import org.http4s.Response
import com.example.common.BookModuleError.{BookNotFound, ValidationFailure}

private[api] trait ExecutionLogger[F[_]] {
  def logStart: F[Unit]
  def log2xx: F[Unit]
  def log4xx(reason: String): F[Unit]
  def log5xx(ex: Throwable): F[Unit]
}

private[api] object ExecutionLogger {
  implicit class ExecutionLoggerOps[F[_]](private val self: F[Response[F]]) extends AnyVal {
    def withLogging(logger: ExecutionLogger[F])(implicit F: MonadError[F, Throwable]): F[Response[F]] =
      logger.logStart >> self.attempt.flatTap {
        case Right(_) =>
          logger.log2xx
        case Left(error: AppError) =>
          error match {
            case ex: BookNotFound =>
              logger.log4xx(ex.getMessage)
            case ex: ValidationFailure =>
              logger.log4xx(ex.getMessage)
          }
        case Left(ex) =>
          logger.log5xx(ex)
      }.rethrow
  }
}
