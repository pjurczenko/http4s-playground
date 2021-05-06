package com.example.domain.book

private trait BookRepository[F[_]] {
  def find(bookId: BookId): F[Option[Book]]
  def insert(book: BookInsert): F[BookId]
  def update(bookId: BookId, book: BookUpdate): F[Unit]
}
