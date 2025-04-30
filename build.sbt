ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "gic-cinemas-booking-system",
    idePackagePrefix := Some("gic.booking.system")
  )

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

Compile / doc / scalacOptions ++= Seq(
  "-doc-title", "GIC Cinemas Booking System",
  "-doc-version", version.value,
  "-project", "gic.booking.system"
)