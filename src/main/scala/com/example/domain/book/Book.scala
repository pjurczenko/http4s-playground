package com.example.domain.book

import com.example.common.BookModuleError.ValidationFailure

private case class Book(id: BookId, title: String, author: String, year: Int) {
  def toDto: BookDto =
    BookDto(id.value, title, author, year)
  def withUpdates(update: BookUpdate): Book =
    copy(title = update.title, author = update.author, year = update.year)
}

private case class BookInsert(title: String, author: String, year: Int) {
  def toBook(bookId: BookId): Book = Book(bookId, title, author, year)
}

private case class BookUpdate(title: String, author: String, year: Int)

private object BookInsert {
  def fromDto(input: BookInsertDto): Either[ValidationFailure, BookInsert] =
    Right(BookInsert(input.title, input.author, input.year))
}

private object BookUpdate {
  def fromDto(input: BookUpdateDto): Either[ValidationFailure, BookUpdate] =
    Right(BookUpdate(input.title, input.author, input.year))
}
