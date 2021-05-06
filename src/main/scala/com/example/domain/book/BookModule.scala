package com.example.domain.book

import cats.effect.Sync
import com.example.domain.book.interpreters.BookModuleFactory

trait BookModule[F[_]] {
  def find(bookId: Int): F[BookDto]
  def insert(bookInsert: BookInsertDto): F[BookIdDto]
  def update(bookId: Int, bookUpdate: BookUpdateDto): F[Unit]
}

object BookModule {
  def create[F[_]: Sync]: F[BookModule[F]] = BookModuleFactory.create[F]
}
