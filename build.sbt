
name := "bank-account-typeclasses"

version := "0.1"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/com.chuusai/shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-MF"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % Test