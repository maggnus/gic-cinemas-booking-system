# 📽️ GIC Cinemas Booking System

A simple Scala 3 CLI application for booking movie tickets in a cinema. Designed using a clean object-oriented architecture and tested using ScalaTest.

## 📁 Project Structure

```
src/
├── main
│   └── scala
│       ├── cinema
│       │   ├── Cinema.scala        # Core cinema logic
│       │   ├── Exception.scala     # Custom domain exceptions
│       │   ├── Hall.scala          # Hall model and layout logic
│       │   ├── Movie.scala         # Movie representation
│       │   ├── Order.scala         # Order model and operations
│       │   ├── Seat.scala          # Seat model and parsing
│       │   └── Show.scala          # Show model
│       ├── cli
│       │   └── BookingCLI.scala    # Command-line interface logic
│       └── main.scala              # Entry point
└── test
    └── scala
        └── CinemaBookingTest.scala # Unit tests
```

## 🚀 Getting Started

### Prerequisites

- [JDK 17+](https://adoptium.net/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/)

### Running the App

```bash
sbt run
```

### Running Tests

```bash
sbt test
```

## 🛠 Features

- Define movies and seating map interactively
- Book tickets via terminal
- Seat assignment and validation
- Error handling (e.g., invalid input, overbooking)
- Check and update bookings

## 🧪 Testing

Tests are written using ScalaTest and are located in:

```
src/test/scala/CinemaBookingTest.scala
```

To execute:

```bash
sbt test
```

## 📦 Packaging

To create a fat JAR:

```bash
sbt assembly
```

Make sure to add the `sbt-assembly` plugin to `project/plugins.sbt`.

## 📄 License

MIT License © 2025 GIC Cinemas
