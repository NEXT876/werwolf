// src/main/scala/Main.scala
package de.htwg.werwolf

import de.htwg.werwolf.model.{addRoles, night}


@main
def main(): Unit = {
  import sys.process._
  import scala.io.Source
  "clear".!

  tiping("Willkommen zu Werwolf", 100)
  val text = Source.fromResource("logo.txt").mkString
  println(text)

  val playerAmount = getplayerAmount(false, 0)
  if playerAmount < 2 || playerAmount > 6 then
    println("\u001b[31mUngültige Spieleranzahl\u001b[0m");
    return;
  val player = getPlayerNames(false, playerAmount, Vector[String]())
  "clear".!
  val playerRoles = addRoles(player)
  val updatedplayerroles = night(playerRoles)
  println(printPlayerRoles(updatedplayerroles))
}
