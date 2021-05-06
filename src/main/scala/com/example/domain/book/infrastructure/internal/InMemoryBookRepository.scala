package com.example.domain.book.infrastructure.internal

import cats.MonadError
import cats.effect.concurrent.Ref
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.example.common.BookModuleError.BookNotFound
import com.example.domain.book.{Book, BookId, BookInsert, BookRepository, BookUpdate}

private[infrastructure] class InMemoryBookRepository[F[_]](
  books: Ref[F, Map[BookId, Book]],
  bookIds: Ref[F, Int]
)(implicit F: MonadError[F, Throwable])
    extends BookRepository[F] {

  override def find(bookId: BookId): F[Option[Book]] =
    books.get.map(_.get(bookId))

  override def insert(bookInsert: BookInsert): F[BookId] =
    nextBookId
      .map(bookInsert.toBook)
      .flatMap(book => books.update(existingBooks => existingBooks + (book.id -> book)).as(book.id))

  override def update(bookId: BookId, bookUpdate: BookUpdate): F[Unit] =
    books.updateAndGet { existingBooks =>
      existingBooks.get(bookId) match {
        case Some(book) => existingBooks.updated(bookId, book.withUpdates(bookUpdate))
        case None => existingBooks
      }
    }.flatMap { updatedBooks =>
      updatedBooks.get(bookId) match {
        case Some(_) => F.unit
        case None => F.raiseError(BookNotFound(bookId.toDto))
      }
    }

  private def nextBookId: F[BookId] =
    bookIds.updateAndGet(_ + 1).flatMap(parseBookId)

  private def parseBookId(bookId: Int): F[BookId] =
    F.fromEither(BookId.fromInt(bookId))

}
