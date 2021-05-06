package com.example.domain.book.infrastructure

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.example.domain.book.{Book, BookId, BookRepository}
import com.example.domain.book.infrastructure.internal.InMemoryBookRepository

private[book] object BookRepositoryFactory {
  def create[F[_]: Sync]: F[BookRepository[F]] =
    for {
      books <- Ref.of(Map.empty[BookId, Book])
      bookIds <- Ref.of(0)
    } yield new InMemoryBookRepository[F](books, bookIds)
}
