package com.example.api.book

import cats.effect.{ContextShift, IO, Timer}
import com.example.domain.book.{BookIdDto, BookInsertDto, BookModule}
import com.example.infrastructure.server.AppRoutes
import io.circe._
import io.circe.parser._
import org.http4s._
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.example.server.util.HttpAppUtil.HttpAppOps
import com.example.server.util.ResponseUtil.ResponseOps

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

//noinspection TypeAnnotation
class BookEndpointSpec extends AnyFlatSpec with Matchers {

  behavior of "GET /books/{id}"

  it should "return `200 OK` for an existing book" in new BookEndpointSetup {
    // Given
    existing(book("Title", "Author", 2021))
    val request = Request[IO](method = Method.GET, uri = uri"/books/1")

    // When
    val response = routes.execute(request)

    // Then
    response.status shouldBe Status.Ok
    response.string shouldBe compact(
      """
        |{
        |  "id": 1,
        |  "title": "Title",
        |  "author": "Author",
        |  "year": 2021
        |}
        |""".stripMargin
    )
  }

  it should "return `404 Not Found` for a non-existing book" in new BookEndpointSetup {
    // Given
    val request = Request[IO](method = Method.GET, uri = uri"/books/123")

    // When
    val response = routes.execute(request)

    // Then
    response.status shouldBe Status.NotFound
    response.string shouldBe compact(
      """
        |{
        |  "code": "BookNotFound",
        |  "message": "Book not found",
        |  "path": "/books/123"
        |}
        |""".stripMargin
    )
  }

  private trait BookEndpointSetup {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    val module = BookModule.create[IO].unsafeRunSync()
    val endpoint = BookEndpoint.create(module).unsafeRunSync()
    val routes: HttpApp[IO] = AppRoutes.create(endpoint, 5.seconds)

    def existing(book: BookInsertDto): BookIdDto =
      module.insert(book).unsafeRunSync()
  }

  private def book(title: String, author: String, year: Int): BookInsertDto =
    BookInsertDto(title, author, year)

  private def compact(json: String): String =
    parse(json).getOrElse(Json.Null).noSpaces

}
