package gic.booking.system

import cli._
import cinema._

@main
def main(): Unit =

  BookingCLI.run()

  /*
  val cinema = Cinema
  val hallId = cinema.addHall(8, 10)
  val movieId = cinema.addMovie("Inception")
  val showId = cinema.addShow(movieId, hallId)

  // [GIC0001] Order 4 tickets
  val orderId1 = cinema.createOrder(showId, 4)
  cinema.printOrder(orderId1)

  // [GIC0001] Change first seat to B3
  cinema.updateOrder(orderId1, showId, 4, Option(Seat('B', 3)))
  cinema.printOrder(orderId1)

  // [GIC0001] Confirm order
  cinema.confirmOrder(orderId1)
  cinema.printOrder(orderId1)

  // [GIC0002] Order 12 tickets
  val orderId2 = cinema.createOrder(showId, 12)
  cinema.printOrder(orderId2)
  // [GIC0002] Change first seat to B5
  cinema.updateOrder(orderId2, showId, 12, Option(Seat('B', 5)))
  cinema.printOrder(orderId2)

  // [GIC0001] Print order
  cinema.printOrder(orderId1)
  */

