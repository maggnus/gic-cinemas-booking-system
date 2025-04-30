package gic.booking.system
package cinema

object Cinema:
  // Maps to store halls, movies, orders, and shows in the cinema system
  private var halls: Map[Int, Hall] = Map.empty
  private var movies: Map[Int, Movie] = Map.empty
  private var orders: Map[OrderId, Order] = Map.empty
  private var shows: Map[Int, Show] = Map.empty

  // Add a new hall with specified rows and seats in each row
  def addHall(rows: Int, seatsInRow: Int): Int =
    val hall = Hall(rows, seatsInRow)
    val newId = halls.keysIterator.maxOption.getOrElse(0) + 1
    halls = halls + (newId -> hall)  // Update the halls map with the new hall
    newId

  // Retrieve a hall by its ID
  def getHall(id: Int): Option[Hall] = halls.get(id)

  // Retrieve a movie by its ID
  def getMovie(id: Int): Option[Movie] = movies.get(id)

  // Retrieve a show by its ID
  def getShow(id: Int): Option[Show] = shows.get(id)

  // Retrieve an order by its ID
  def getOrder(orderId: OrderId): Option[Order] = orders.get(orderId)

  // Add a new movie and return its ID
  def addMovie(title: String): Int =
    val movie = Movie(title)
    val newId = movies.keysIterator.maxOption.getOrElse(0) + 1
    movies = movies + (newId -> movie)  // Update the movies map with the new movie
    newId

  // Add a new show for a movie in a hall and return the show ID
  def addShow(movieId: Int, hallId: Int): Int =
    val show = Show(movieId, hallId)
    val newId = shows.keysIterator.maxOption.getOrElse(0) + 1
    shows = shows + (newId -> show)  // Update the shows map with the new show
    newId

  // Add an order, cancelling any existing order with the same ID first
  private def addOrder(orderId: OrderId, showId: Int, tickets: Int, firstSeat: Option[Seat] = None): OrderId =
    cancelOrder(orderId)  // Cancel any existing order with the same ID
    val seats = getSeats(showId, tickets, firstSeat)  // Get the seats for the order
    val order = Order(showId, seats, OrderStatus.Reserved)  // Create a new reserved order
    orders += (orderId -> order)  // Update the orders map
    orderId

  // Cancel an order by its ID
  private def cancelOrder(orderId: OrderId): Unit =
    if orders.contains(orderId) then
      orders -= orderId  // Remove the order if it exists

  // Create a new order and return its order ID
  def createOrder(showId: Int, tickets: Int, firstSeat: Option[Seat] = None): OrderId =
    val orderId = orders.keysIterator.maxOption.getOrElse(0) + 1
    addOrder(orderId, showId, tickets, firstSeat)  // Add the order and return the ID

  // Update an existing order
  def updateOrder(orderId: OrderId, showId: Int, tickets: Int, firstSeat: Option[Seat] = None): OrderId =
    addOrder(orderId, showId, tickets, firstSeat)  // Add the updated order and return the ID

  // Confirm an order by updating its status to "Confirmed"
  def confirmOrder(orderId: OrderId): Unit =
    val order = getOrder(orderId).getOrElse(throw OrderNotFound)  // Get the order or throw exception
    val updatedOrder = order.copy(status = OrderStatus.Confirmed)  // Update the status to confirmed
    orders += (orderId -> updatedOrder)  // Update the orders map

  // Calculate the number of available seats for a show
  def availableSeats(showId: Int): Int =
    val show = getShow(showId).getOrElse(throw ShowNotFound)  // Get the show or throw exception
    val hall = getHall(show.hallId).getOrElse(throw HallNotFound)  // Get the hall or throw exception

    val totalSeats = hall.rows * hall.seatsInRow  // Total number of seats in the hall
    val takenSeats = orders.values
      .filter(_.showId == showId)
      .flatMap(_.seats)
      .toSet  // Collect all taken seats for the show

    totalSeats - takenSeats.size  // Return the number of available seats

  // Get the seats for a given order, considering the tickets and optional first seat
  private def getSeats(showId: Int, tickets: Int, firstSeat: Option[Seat]): List[Seat] =
    val show = getShow(showId).getOrElse(throw ShowNotFound)  // Get the show or throw exception
    val hall = getHall(show.hallId).getOrElse(throw HallNotFound)  // Get the hall or throw exception

    val takenSeats: Set[Seat] = orders
      .filter(_._2.showId == showId)
      .flatMap(_._2.seats)
      .toSet  // Collect all taken seats for the show

    // Helper function to check if a block of seats is free
    def areSeatsFree(seatBlock: List[Seat]): Boolean =
      seatBlock.forall(!takenSeats.contains(_))  // Check if all seats in the block are free

    val allRows = ('A' until ('A' + hall.rows).toChar).toList  // List of rows in the hall

    // Handling case when the first seat is provided
    firstSeat match
      case Some(start) =>
        val startRowIdx = allRows.indexOf(start.row)
        if startRowIdx == -1 then throw InvalidStartingRow  // Throw exception if start row is invalid

        var remaining = tickets  // Remaining tickets to allocate
        var selected: List[Seat] = List.empty  // List of selected seats

        // Iterate over rows from the starting row
        for row <- allRows.drop(startRowIdx) if remaining > 0 do
          val startCol = if row == start.row then start.number else 1
          val endCol = hall.seatsInRow

          // Find free seats in the row
          val freeSeats = (startCol to endCol)
            .map(n => Seat(row, n))
            .filter(!takenSeats.contains(_))
            .take(remaining)  // Take only the remaining number of seats

          selected ++= freeSeats  // Add the free seats to the selected list
          remaining -= freeSeats.length  // Update the remaining tickets

        // If enough seats were selected, return them, otherwise throw an exception
        if selected.length == tickets then selected
        else throw NotEnoughSeats

      case None =>
        // Center-based seat allocation when no first seat is specified
        val center = (hall.seatsInRow + 1) / 2
        var remaining = tickets  // Remaining tickets to allocate
        var selected: List[Seat] = List.empty  // List of selected seats

        for row <- allRows if remaining > 0 do
          // Find blocks of seats that can accommodate the remaining tickets
          val freeBlocks = (1 to hall.seatsInRow)
            .sliding(remaining, 1)
            .filter(_.last <= hall.seatsInRow)
            .map(_.map(n => Seat(row, n)).toList)
            .filter(areSeatsFree)
            .toList
            .sortBy(block => math.abs(block.map(_.number).sum.toDouble / block.size - center))

          freeBlocks.headOption match
            case Some(block) =>
              selected ++= block  // Select the best block of seats
              remaining -= block.length  // Update the remaining tickets
            case None =>
              // If no block is found, select individual seats
              val smallerBlocks = (1 to hall.seatsInRow)
                .sliding(1, 1)
                .map(n => Seat(row, n.head))
                .filter(!takenSeats.contains(_))
                .take(remaining)
                .toList

              selected ++= smallerBlocks  // Add smaller blocks of individual seats
              remaining -= smallerBlocks.length  // Update the remaining tickets

        // If enough seats were selected, return them, otherwise throw an exception
        if selected.length == tickets then selected
        else throw NotEnoughSeats

  // Print the details of an order
  def printOrder(orderId: OrderId): Unit =
    val order = getOrder(orderId).getOrElse(throw OrderNotFound)  // Get the order or throw exception
    val show = getShow(order.showId).getOrElse(throw ShowNotFound)  // Get the show or throw exception
    val hall = getHall(show.hallId).getOrElse(throw HallNotFound)  // Get the hall or throw exception

    println(f"Booking id: ${formatOrderId(orderId)}")  // Print the booking ID
    println("Selected seats:\n")  // Print a header

    val rows = hall.rows
    val seatsInRow = hall.seatsInRow
    val spaces = List.fill(seatsInRow / 2 - 2)(" ").mkString("\t")  // Calculate spaces for screen alignment

    val currentSeats = order.seats.toSet  // Get the current selected seats

    // Get all taken seats (other orders)
    val allTakenSeats = orders
      .filter((id, o) => o.showId == order.showId && id != orderId)
      .flatMap((_, o) => o.seats)
      .toSet

    // Print screen header
    println(f"$spaces\t S C R E E N")
    val dashes = List.fill(seatsInRow + 1)("-").mkString("\t")
    println(dashes)

    // Print the hall seat layout
    for row <- ('A' until ('A' + rows).toChar).toList.reverse do
      val rowSeats = (1 to seatsInRow).map { seatNum =>
        val seat = Seat(row, seatNum)
        if currentSeats.contains(seat) then "o"  // Mark selected seats as "o"
        else if allTakenSeats.contains(seat) then "#"  // Mark taken seats as "#"
        else "."  // Mark free seats as "."
      }.mkString("\t")
      println(s"$row\t$rowSeats")  // Print each row with its seat status
    println("\t" + (1 to seatsInRow).map(_.toString).mkString("\t"))  // Print seat numbers
    println()