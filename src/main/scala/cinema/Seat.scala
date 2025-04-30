package gic.booking.system
package cinema

case class Seat(row: Char, number: Int):
  def index: String = s"$row$number"

object Seat:
  def fromString(s: String): Option[Seat] =
    val SeatPattern = """^([A-Z])(\d{2})$""".r
    s match
      case SeatPattern(rowChar, numStr) =>
        Some(Seat(rowChar.head, numStr.toInt))
      case _ => None