package com.example.infrastructure.server.middleware.common

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

private[middleware] object ErrorCodecs {
  implicit val errorDtoEncoder: Encoder[ErrorDto] = deriveEncoder
}
