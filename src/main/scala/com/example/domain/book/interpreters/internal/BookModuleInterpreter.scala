package com.example.domain.book.interpreters.internal

import cats.MonadError
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.example.common.BookModuleError.BookNotFound
import com.example.domain.book.{BookDto, BookId, BookIdDto, BookInsert, BookInsertDto, BookModule, BookRepository, BookUpdate, BookUpdateDto}

private[interpreters] class BookModuleInterpreter[F[_]](
  repository: BookRepository[F]
)(implicit F: MonadError[F, Throwable])
    extends BookModule[F] {

  override def find(bookId: Int): F[BookDto] =
    F.fromEither(BookId.fromInt(bookId)).flatMap { domainBookId =>
      repository.find(domainBookId).flatMap {
        case Some(book) => F.pure(book.toDto)
        case None => F.raiseError(BookNotFound(domainBookId.toDto))
      }
    }

  override def insert(bookInsert: BookInsertDto): F[BookIdDto] =
    F.fromEither(BookInsert.fromDto(bookInsert))
      .flatMap(repository.insert)
      .map(_.toDto)

  override def update(bookId: Int, bookUpdate: BookUpdateDto): F[Unit] =
    for {
      domainBookId <- F.fromEither(BookId.fromInt(bookId))
      domainBookUpdate <- F.fromEither(BookUpdate.fromDto(bookUpdate))
      _ <- repository.update(domainBookId, domainBookUpdate)
    } yield ()

}
