package com.example.common

import com.example.domain.book.BookIdDto

sealed trait AppError
sealed trait BookModuleError extends AppError

object BookModuleError {
  final case class BookNotFound(bookId: BookIdDto) extends Exception(s"book not found") with BookModuleError
  final case class ValidationFailure(reason: String) extends Exception(reason) with BookModuleError
}
