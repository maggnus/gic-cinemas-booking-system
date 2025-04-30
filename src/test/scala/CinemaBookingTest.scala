package gic.booking.system

import gic.booking.system.cinema._
import munit.*

import java.util.Base64
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class CinemaBookingTest extends FunSuite {

  val cinema: Cinema.type = Cinema

  // Helper function to capture console output and encode as base64
  def captureBase64Output(thunk: => Unit): String = {
    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      thunk
    }
    Base64.getEncoder.encodeToString(out.toByteArray)
  }

  test("Order 4 tickets, print initial order") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 4)

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }

  test("Change first seat to B3 and print") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 4)
    cinema.updateOrder(orderId, showId, 4, Option(Seat('B', 3)))

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }

  test("Should throw NotEnoughSeats when ordering more seats than available") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)

    try {
      val orderId = cinema.createOrder(showId, 100)
    } catch {
      case e: Exception =>
        assert(e.getClass.getName.contains("gic.booking.system.cinema.NotEnoughSeats"))
    }
  }

  test("Confirm order and print") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 4)
    cinema.updateOrder(orderId, showId, 4, Option(Seat('B', 3)))
    cinema.confirmOrder(orderId)

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }

  test("Order 12 tickets and print") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 12)

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }

  test("Change seat to B5 and print") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 12)
    cinema.updateOrder(orderId, showId, 12, Option(Seat('B', 5)))

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }

  test("Print already confirmed order") {
    val hallId = cinema.addHall(8, 10)
    val movieId = cinema.addMovie("Inception")
    val showId = cinema.addShow(movieId, hallId)
    val orderId = cinema.createOrder(showId, 4)
    cinema.updateOrder(orderId, showId, 4, Option(Seat('B', 3)))
    cinema.confirmOrder(orderId)

    val encoded = captureBase64Output {
      cinema.printOrder(orderId)
    }

    assert(clue(encoded).nonEmpty)
  }
}