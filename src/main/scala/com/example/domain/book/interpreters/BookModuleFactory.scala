package com.example.domain.book.interpreters

import cats.effect.Sync
import cats.syntax.functor._
import com.example.domain.book.BookModule
import com.example.domain.book.infrastructure.BookRepositoryFactory
import com.example.domain.book.interpreters.internal.BookModuleInterpreter

private[book] object BookModuleFactory {
  def create[F[_]: Sync]: F[BookModule[F]] =
    BookRepositoryFactory.create[F].map(new BookModuleInterpreter(_))
}
