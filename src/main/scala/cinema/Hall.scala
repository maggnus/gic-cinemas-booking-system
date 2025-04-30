package gic.booking.system
package cinema

case class Hall(rows: Int, seatsInRow: Int):
  var capacity: Int = rows * seatsInRow
