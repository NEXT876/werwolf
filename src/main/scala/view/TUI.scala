// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import scala.io.StdIn.readLine
import scala.io.Source

import de.htwg.werwolf.model.{Observer, GameEvent, Game, Player}
import de.htwg.werwolf.controller.GameController

class TUI(game: Game, controller: GameController) extends Observer {
  game.addObserver(this)
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

    val playerAmount = getplayerAmount(false, 0)
    if playerAmount < 2 || playerAmount > 6 then
      println("\u001b[31mUngültige Spieleranzahl\u001b[0m");
      return;
    val players = getPlayerNames(false, playerAmount, Vector[String]())
    clearScreen()
    controller.startGame(players)
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
  
  def getplayerAmount(test: Boolean, fakeInput: Int): Int = {
    val playerAmount =
      if test then fakeInput
      else
        readLine(
          "Bitte Spieleranzahl eingeben ( Mindestanzahl 2, Maximale Spieleranzahl 7): "
        ).toInt
    playerAmount
  }

  def getPlayerNames(
      test: Boolean,
      playerAmount: Int,
      fakeInput: Vector[String]
  ): Vector[String] = {
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

  private def showLogo(): Unit = {
    val text = Source.fromResource("logo.txt").mkString
    println(text)
  }

  private def clearScreen(): Unit = {
    import sys.process._
    "clear".!
  }
}
