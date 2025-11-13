package de.htwg.werwolf.View
import de.htwg.werwolf.model.Player

import scala.io.StdIn.readLine

def printPlayerRoles(playerRoles: Map[String, Player]): String =
  val header = "\n================ Spieler & Rollen ================\n"
  val body = playerRoles
    .map { case (name, player) =>
      val role = player.role
      val state = player.isAlive
      f"• $name%-15s | Rolle: $role%-10s | Status: ${if player.isAlive then "lebt" else "tot"}%-7s"
    }
    .mkString("\n")
  val footer = "\n==================================================\n"
  header + body + footer

def getplayerAmount(test: Boolean, fakeInput: Int): Int = {
  val playerAmount =
    if test then fakeInput
    else
      readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2, Maximale Spieleranzahl 7): ").toInt
  playerAmount
}

def getPlayerNames(test: Boolean, playerAmount: Int, fakeInput: Vector[String]): Vector[String] = {
  if test then fakeInput
  else
    (0 until playerAmount)
      .foldLeft(Set[String]()) { (acc, i) =>
        var name = readLine(s"Spieler ${i + 1} bitte geben sie ihren Namen an: ").trim
        while (acc.contains(name)) {
          println("Name gibt es bereits, bitte wähle einen anderen")
          name = readLine(s"Spieler ${i + 1} bitte geben sie ihren Namen an: ").trim
        }

        acc + name
      }
      .toVector
}

def tiping(text: String, waitTime_ms: Int): Boolean = {
  text.foreach { buchstabe =>
    if (buchstabe == '.') {
      Thread.sleep(waitTime_ms * 4)
      print(buchstabe)
    } else {
      print(buchstabe)
      Thread.sleep(waitTime_ms)
    }
  }
  println()
  true
}
