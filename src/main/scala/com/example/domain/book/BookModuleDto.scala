package com.example.domain.book

case class BookIdDto(id: Int)
case class BookDto(id: Int, title: String, author: String, year: Int)
case class BookInsertDto(title: String, author: String, year: Int) {
  override def toString: String = s"title=$title, author=$author, year=$year"
}
case class BookUpdateDto(title: String, author: String, year: Int) {
  override def toString: String = s"title=$title, author=$author, year=$year"
}
