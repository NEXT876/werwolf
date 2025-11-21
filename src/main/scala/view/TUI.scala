// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import de.htwg.werwolf.model.*
import de.htwg.werwolf.controller.GameController

import scala.io.StdIn.readLine

class TUI(controller: GameController) extends Observer[GameEvent] {
  override def update(event: GameEvent): Unit =
    //val state = controller.getGame.currentState

    event match
      case GameEvent.printGameState(alivePlayers) =>
        clearScreen()
        showLogo()
        println(printPlayerRoles(alivePlayers))
      case GameEvent.phaseSwitch(phase)  =>
      //
      case GameEvent.gameEnd(isRunning) =>
        if(!isRunning)
          println("Das Spiel ist vorbei")
          System.exit(0)

  def start(): Unit = {
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()

    val names = getPlayerNames(getPlayerAmount())

    clearScreen()
    controller.initializePlayers(names)
    run()
  }

  def run(): Unit =
    while (true) {
      controller.process("")
      controller.process("switchPhase")
      //
      //
      controller.process("GameEnd")
    }

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

  def printPlayerRoles(playerRoles: Map[String, Player]): String = {
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
  }

  def tiping(text: String, waitTime_ms: Int): Unit = {
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

  private def showLogo(): Unit = {
    import scala.io.Source
    println(Source.fromResource("logo.txt").mkString)
  }

  private def clearScreen(): Unit = {
    import sys.process._
    if (sys.props("os.name").toLowerCase.contains("win")) "cls".!
    else "clear".! // clear Screen for WIndows and Linux/Mac
  }
}
