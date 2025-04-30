package gic.booking.system
package cinema

type OrderId = Int

def extractOrderId(code: String): Option[OrderId] =
  ".*?(\\d+)$".r.findFirstMatchIn(code).map(_.group(1).toInt)

def formatOrderId(id: Int): String =
  f"GIC$id%04d"

enum OrderStatus:
  case Reserved, Confirmed

case class Order(showId: Int, seats: List[Seat], status: OrderStatus = OrderStatus.Reserved)

