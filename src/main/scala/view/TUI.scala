// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.gameControllerComponent.GameControllerInterface
import de.htwg.werwolf.util.Observer


import scala.io.StdIn.readLine

class TUI(controller : GameControllerInterface) extends Observer[GameEvent] {

  override def update(event: GameEvent): Unit =
  event match
    case GameEvent.printGameState(players) =>
      clearScreen()
      showLogo()
      printPlayerRoles(players)

    case GameEvent.printnarratorText(text) =>
      showLogo()
      tiping(text)
    
    case GameEvent.printText(text,wait) =>
      tiping(text,wait)

    case GameEvent.showLogo =>
      showLogo()

    case GameEvent.clearScreen =>
      clearScreen()

    case GameEvent.requestPlayerNames =>
      

    case GameEvent.GameOver =>
      readLine("Press any key to end the game")
      clearScreen()
      showGameOver()
      javafx.application.Platform.exit()

    case GameEvent.printErrorMSG(msg) =>
      printErrorMsg(msg)

    case _ =>

  def start(): Unit = 
    Thread.sleep(1000)
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()
    controller.saveGameState()

    val names = getPlayerNames(getPlayerAmount())
    clearScreen()
    controller.addRoles(names)

    controller.runGame()
  
  def getPlayerAmount(): Int = 
    val input = readLine("Wie viele Spieler? (mind. 2, max. 6): ")
    input.toIntOption match {
      case Some(n) => n.min(6).max(2)
      case None    => 5
    }
  
  def getPlayerNames(playerAmount: Int): Vector[String] = 
    (0 until playerAmount).map { i =>
      readLine(s"Spieler${i + 1} wie heißen sie:").trim match
        case "" => s"Spieler${i + 1}"
        case n  => n
    }.toVector
  
  def printPlayerRoles(playerRoles: String): Unit = 
    val header = "\n================ Spieler & Rollen ==================\n"
    val footer = "\n====================================================\n"

    println(header + playerRoles + footer)
  
  def tiping(text: String, waitTime_ms: Int = 30): Unit = 
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
  
  def showLogo(): Unit = 
    import scala.io.Source
    println(Source.fromResource("logo.txt").mkString)

  def clearScreen(): Unit = 
    import sys.process._
    if (sys.props("os.name").toLowerCase.contains("win")) "cls".!
    else "clear".! // clear Screen for WIndows and Linux/Mac
  
  def showGameOver(): Unit =
    println("Das Game ist vorbei")

  def printErrorMsg(msg: String): Unit =
    println(msg)
}
