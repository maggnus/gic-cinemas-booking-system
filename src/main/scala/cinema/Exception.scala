package gic.booking.system
package cinema

sealed trait SeatAllocationError extends Exception {
  def message: String
  override def getMessage: String = message
}

case object ShowNotFound extends SeatAllocationError {
  val message: String = "Show not found"
}

case object HallNotFound extends SeatAllocationError {
  val message: String = "Hall not found"
}

case object MovieNotFound extends SeatAllocationError {
  val message: String = "Movie not found"
}

case object OrderNotFound extends SeatAllocationError {
  val message: String = "Order not found"
}

case object InvalidStartingRow extends SeatAllocationError {
  val message: String = "Invalid starting row"
}

case object NotEnoughSeats extends SeatAllocationError {
  val message: String = "Not enough seats available"
}