package com.example.api.book

import com.example.domain.book.{BookDto, BookIdDto, BookInsertDto, BookUpdateDto}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

private object BookEndpointCodecs extends BookEndpointEncoders with BookEndpointDecoders

private trait BookEndpointEncoders {
  implicit val bookIdDtoEncoder: Encoder[BookIdDto] = deriveEncoder
  implicit val bookDtoEncoder: Encoder[BookDto] = deriveEncoder
}

private trait BookEndpointDecoders {
  implicit val bookInsertDtoDecoder: Decoder[BookInsertDto] = deriveDecoder
  implicit val bookUpdateDtoDecoder: Decoder[BookUpdateDto] = deriveDecoder
}