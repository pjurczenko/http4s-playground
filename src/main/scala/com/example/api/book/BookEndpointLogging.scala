package com.example.api.book

import com.example.api.common.ExecutionLogger
import com.example.domain.book.{BookInsertDto, BookUpdateDto}
import org.typelevel.log4cats.Logger

// TODO: introduce `extends AnyVal`
private object BookEndpointLogging {

  case class FindBookLogger[F[_]](bookId: Int)(implicit logger: Logger[F]) extends ExecutionLogger[F] {
    override def logStart: F[Unit] =
      logger.info(s"Retrieving a book: bookId=$bookId")
    override def log2xx: F[Unit] =
      logger.info(s"Successfully retrieved a book: bookId=$bookId")
    override def log4xx(reason: String): F[Unit] =
      logger.warn(s"Failed to retrieve a book: bookId=$bookId, reason=$reason")
    override def log5xx(ex: Throwable): F[Unit] =
      logger.error(ex)(s"Failed to retrieve a book: bookId=$bookId, reason=${ex.getMessage}")
  }

  case class InsertBookLogger[F[_]](bookInsert: BookInsertDto)(implicit logger: Logger[F]) extends ExecutionLogger[F] {
    override def logStart: F[Unit] =
      logger.info(s"Inserting a book: $bookInsert")
    override def log2xx: F[Unit] =
      logger.info(s"Successfully inserted a book: $bookInsert")
    override def log4xx(reason: String): F[Unit] =
      logger.warn(s"Failed to insert a book: $bookInsert, reason=$reason")
    override def log5xx(ex: Throwable): F[Unit] =
      logger.error(ex)(s"Failed to insert a book: $bookInsert, reason=${ex.getMessage}")
  }

  case class UpdateBookLogger[F[_]](bookId: Int, bookUpdate: BookUpdateDto)(implicit logger: Logger[F])
      extends ExecutionLogger[F] {
    override def logStart: F[Unit] =
      logger.info(s"Updating the book: bookId=$bookId, $bookUpdate")
    override def log2xx: F[Unit] =
      logger.info(s"Successfully updated the book: bookId=$bookId, $bookUpdate")
    override def log4xx(reason: String): F[Unit] =
      logger.warn(s"Failed to update the book: bookId=$bookId, $bookUpdate, reason=$reason")
    override def log5xx(ex: Throwable): F[Unit] =
      logger.error(ex)(s"Failed to update the book: bookId=$bookId, $bookUpdate, reason=${ex.getMessage}")
  }

}
