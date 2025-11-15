// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import de.htwg.werwolf.model.*
import de.htwg.werwolf.controller.GameController

import scala.io.StdIn.readLine
import scala.io.Source

class TUI(controller: GameController) extends Observer[GameState] {
  override def update(state : GameState): Unit = 
    if state.isRunning then
      clearScreen()
      println(printPlayerRoles(state.alivePlayers))

      state.phase match 
        case Phase.Night => 
        case Phase.Day => 
      
    else 
      println("Game zuende")
      System.exit(0)

  def start(): Unit = {
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()

    val numPlayers = readLine("Wie viele Spieler? (mind. 2, max. 6):").toIntOption.getOrElse(5) max 6 min 2

    val names = (0 to numPlayers).map { i =>
      readLine(s"Spieler${i+1} wie heißen sie:").trim match
        case "" => s"Spieler$i"
        case n  => n
    }.toVector
    clearScreen()
    controller.addRoles(names)
    run()
  }

  def run() : Unit = 
      while(true){
        controller.process("switchPhase")
        controller.process("GameEnd")
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
    val text = Source.fromResource("logo.txt").mkString
    println(text)
  }

  private def clearScreen(): Unit = {
    import sys.process._
    "clear".!
  }
}
