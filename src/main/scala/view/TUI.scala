// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import scala.io.StdIn.readLine
import scala.io.Source

import de.htwg.werwolf.model.*
import de.htwg.werwolf.controller.GameController

class TUI(controller: GameController) extends Observer {
  override def update(event: GameEvent): Unit = event match {
    case GameEvent.NightPhaseStarted(roles) => {
      val data =
        game.NarratorService.loadNarratorJson(
          os.pwd / "src" / "main" / "resources" / "narrator.json"
        )
      val text = game.NarratorService.randomNarratorText("Werwolf", data)
      tiping(text, 30)
    }
    case GameEvent.WerewolfTurn(name, roles) => {
      println(printPlayerRoles(roles))
    }

    case GameEvent.GameStart(roles) => {
      println(printPlayerRoles(roles))
    }
  }

  def start(): Unit = {
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()

    println("Wie viele Spieler? (mind. 5, max. 12)")
    val numPlayers = readLine().toIntOption.getOrElse(5) max 5 min 12

    val names = (1 to numPlayers).map { i =>
      println(s"Spieler${i+1} wie heißen sie:")
      readLine().trim match
        case "" => s"Spieler$i"
        case n  => n
    }.toVector
    clearScreen()
    controller.addRoles(names)
    run()
  }

  def run() : Unit{
    
      //GameLoop
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



  def askForPlayerName(playerIndex: Int): String =
    print(s"Spieler ${playerIndex + 1} bitte geben sie ihren Namen an: ")
    scala.io.StdIn.readLine().trim

  def showDuplicateNameWarning(): Unit =
    println("Name gibt es bereits, bitte wähle einen anderen")

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

  private def showLogo(): Unit = {
    val text = Source.fromResource("logo.txt").mkString
    println(text)
  }

  private def clearScreen(): Unit = {
    import sys.process._
    "clear".!
  }
}
