package com.example.api.book

import cats.effect.{Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import BookEndpointCodecs._
import BookEndpointLogging.{FindBookLogger, InsertBookLogger, UpdateBookLogger}
import com.example.api.common.ExecutionLogger.ExecutionLoggerOps
import com.example.domain.book.{BookInsertDto, BookModule, BookUpdateDto}

final class BookEndpoint[F[_]: Sync: Logger: Timer](bookModule: BookModule[F]) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] =
    HttpRoutes
      .of[F] {
        case GET -> Root / "books" / IntVar(bookId) =>
          findBook(bookId)
        case request @ POST -> Root / "books" =>
          request.decodeJson[BookInsertDto].flatMap(insertBook)
        case request @ PUT -> Root / "books" / IntVar(bookId) =>
          request.decodeJson[BookUpdateDto].flatMap(updateBook(bookId))
      }

  private def findBook(bookId: Int): F[Response[F]] =
    bookModule
      .find(bookId)
      .flatMap(book => Ok(book.asJson))
      .withLogging(FindBookLogger(bookId))

  private def insertBook(bookInsert: BookInsertDto): F[Response[F]] =
    bookModule
      .insert(bookInsert)
      .flatMap(bookId => Created(bookId.asJson))
      .withLogging(InsertBookLogger(bookInsert))

  private def updateBook(bookId: Int)(bookUpdate: BookUpdateDto): F[Response[F]] =
    bookModule
      .update(bookId, bookUpdate)
      .flatMap(_ => NoContent())
      .withLogging(UpdateBookLogger(bookId, bookUpdate))

}

object BookEndpoint {
  def create[F[_]: Sync: Timer](bookModule: BookModule[F]): F[BookEndpoint[F]] =
    Slf4jLogger.create[F].map(implicit logger => new BookEndpoint(bookModule))
}
