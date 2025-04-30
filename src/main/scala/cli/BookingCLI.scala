package gic.booking.system
package cli

import cinema.*

import scala.annotation.tailrec
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}
import scala.util.boundary, boundary.break

object BookingCLI {
  val cinema: Cinema.type = Cinema

  @tailrec
  def run(): Unit =
    val pattern: Regex = """(.+?)\s+(\d+)\s+(\d+)$""".r
    prompt("Please define movie title and seating map in [Title] [Row] [SeatsPerRow] format:") match
      case Some(input) =>
        input match
          case pattern(title, rowStr, seatsStr) =>
            val movieId = cinema.addMovie(title)
            val hallId = cinema.addHall(rowStr.toInt, seatsStr.toInt)
            val showId = cinema.addShow(movieId, hallId)
            menu(showId)
          case _ =>
            println("Invalid input format")
            run()
      case None =>
        println("No input received")
        run()

  private def menu(showId: Int): Unit  = boundary:
    val showOpt = cinema.getShow(showId)
    val show = showOpt.getOrElse {
      println(s"Show $showId not found.")
      break(())
    }
    val movie = cinema.getMovie(show.movieId).getOrElse {
      println(s"Movie with id ${show.movieId} not found.")
      break(())
    }
    val hall = cinema.getHall(show.hallId).getOrElse {
      println(s"Hall with id ${show.hallId} not found.")
      break(())
    }
    val capacity = cinema.availableSeats(showId)
    val menu =
      f"""
         |Welcome to GIC Cinemas
         |[1] Book tickets for ${movie.title} ($capacity seats available)
         |[2] Check bookings
         |[3] Exit
         |""".stripMargin
    print(menu)
    prompt("Please enter your selection:") match
      case Some("1") =>
        book(showId)
      case Some("2") =>
        check(showId)
      case Some("3") =>
        println("\nThank you for using GIC Cinemas system. Bye!")
      case _ =>
        println("Invalid selection.")
        menu(showId)

  def book(showId: Int): Unit = boundary:
    val show = cinema.getShow(showId).getOrElse {
      println(s"Show with ID $showId not found.")
      break(())
    }
    val movie = cinema.getMovie(show.movieId).getOrElse {
      println(s"Movie with ID ${show.movieId} not found.")
      break(())
    }
    prompt("Enter number of tickets to book, or enter blank to go back to main menu:") match
      case Some("") =>
        menu(showId)
      case Some(input) =>
        Try(input.toInt) match
          case Success(tickets) if tickets > 0 =>
            val availableSeats = cinema.availableSeats(showId)
            if tickets > availableSeats then
              println(s"Sorry, there are only $availableSeats seats available.")
              book(showId)
            else
              val orderId = cinema.createOrder(showId, tickets)
              println(s"\nSuccessfully reserved $tickets ticket(s) for '${movie.title}'")
              cinema.printOrder(orderId)
              update(showId, orderId)
          case Success(_) =>
            println("Number of tickets must be greater than 0.")
            book(showId)
          case Failure(_) =>
            println("Invalid input format, please enter a number.")
            book(showId)
      case None =>
        println("No input received.")
        book(showId)

  def check(showId: Int): Unit =
    prompt("Enter number of tickets to book, or enter blank to go back to main menu:") match
      case Some("") =>
        menu(showId)
      case Some(input) =>
        extractOrderId(input) match
          case Some(orderId) =>
            cinema.printOrder(orderId)
            book(showId)
          case None =>
            println("Invalid seat format. Please try again.")
            book(showId)
      case None =>
        println("No input received.")
        book(showId)

  def update(showId: Int, orderId: OrderId): Unit = boundary:
    val order = cinema.getOrder(orderId).getOrElse {
      println(s"Order with ID $orderId not found.")
      break(())
    }
    val show = cinema.getShow(showId).getOrElse {
      println(s"Show with ID $showId not found.")
      break(())
    }

    prompt("Enter blank to accept seat selection, or enter new seating position:") match
      case Some("") =>
        println(s"\nBooking ID ${formatOrderId(orderId)} confirmed.")
        menu(showId)
      case Some(seatPositionStr: String) =>
        val setPosition = Seat.fromString(seatPositionStr)
        cinema.updateOrder(orderId, showId, order.seats.length, setPosition)
        update(showId, orderId)
      case None =>
        println("No input received.")
        update(showId, orderId)


  def order(orderId: OrderId): Unit = boundary:
    val order: Order = cinema.getOrder(orderId).getOrElse {
      println(s"Order $orderId not found.")
      break(())
    }
    println(s"Successfully reserved ${order.seats.length} tickets.")
    cinema.printOrder(orderId)

  def prompt(message: String): Option[String] =
    print(s"$message\n> ")
    scala.io.StdIn.readLine() match
      case null => None
      case input => Some(input.trim)

}
