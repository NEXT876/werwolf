// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import scala.io.StdIn.readLine

class TUI() extends GameView {

  def getPlayerAmount(): Int = {
    val input = readLine("Wie viele Spieler? (mind. 2, max. 6): ")
    input.toIntOption match {
      case Some(n) => n.min(6).max(2)
      case None    => 5
    }
  }

  def getPlayerNames(playerAmount: Int): Vector[String] = {
    (0 until playerAmount).map { i =>
      readLine(s"Spieler${i + 1} wie heißen sie:").trim match
        case "" => s"Spieler${i + 1}"
        case n  => n
    }.toVector
  }

  def printPlayerRoles(playerRoles: Vector[AnyRef]): Unit = {
    val header = "\n================ Spieler & Rollen ================\n"

    val body = playerRoles
      .map {
        case (name: String, role: String, isAlive: Boolean) =>
          val state = if isAlive then "lebt" else "tot"
          f"• ${name}%-15s | Rolle: ${role}%-10s | Status: ${state}%-7s"

        case other =>
          s"• Unbekanntes Objekt: ${other.getClass.getSimpleName}"
      }
      .mkString("\n")

    val footer = "\n==================================================\n"

    println(header + body + footer)
  }

  def tiping(text: String, waitTime_ms: Int = 30): Unit = {
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
  }

  def showLogo(): Unit = {
    import scala.io.Source
    println(Source.fromResource("logo.txt").mkString)
  }

  def clearScreen(): Unit = {
    import sys.process._
    if (sys.props("os.name").toLowerCase.contains("win")) "cls".!
    else "clear".! // clear Screen for WIndows and Linux/Mac
  }

  def showGameOver(): Unit =
    println("Das Game ist vorbei")

  def printErrorMsg(msg: String): Unit =
    println(msg)
  
}
