package com.example.domain.book

import com.example.common.BookModuleError.ValidationFailure

private sealed abstract case class BookId(value: Int) {
  override def toString: String = value.toString
  def toDto: BookIdDto = BookIdDto(value)
}

private object BookId {
  def fromInt(input: Int): Either[ValidationFailure, BookId] =
    if (input > 0) Right(new BookId(input) {})
    else Left(ValidationFailure(s"book identifier must be a positive integer"))
}
