// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Observer

import scala.io.StdIn.{readLine, readInt}
import de.htwg.werwolf.model.Roles

class TUI()(using controller: GameControllerInterface) extends Observer[GameEvent] {

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(players) =>
        clearScreen()
        showLogo()
        printPlayerRoles(players)

      case GameEvent.printNarratorText(text) =>
        showLogo()
        tiping(text)

      case GameEvent.printText(text, wait) =>
        tiping(text, wait)

      case GameEvent.showLogo =>
        showLogo()

      case GameEvent.clearScreen =>
        clearScreen()

      case GameEvent.requestPlayerNames =>

      case GameEvent.gameOver =>
        readLine("Press any key to end the game")
        clearScreen()
        showGameOver()
        javafx.application.Platform.exit()

      case GameEvent.printErrorMSG(msg) =>
        printErrorMsg(msg)

      case GameEvent.askForTargetNight(name, role, targets) =>
        if targets.nonEmpty then
          role match
            case Roles.amor =>
            // amorAction(name, targets)
            case Roles.werwolf =>
              werwolfAction(name, targets)
            case Roles.witch =>
              witchAction(name, targets)
            case _ =>

      case GameEvent.askForTargetDay(name, targets) =>
        println(s"$name, für wen votest du")
        targets.zipWithIndex.foreach { case (t, i) =>
          println(s"$i: $t")
        }
        val choice = readValidChoice(targets)
        controller.submitvoting(name, targets(choice))

      case _ =>

  def werwolfAction(name: String, targets: Vector[String]): Unit =
    println(s"$name, welches Opfer reißt du diese Nacht?")
    targets.zipWithIndex.foreach { case (t, i) =>
      println(s"$i: $t")
    }
    val choice = readValidChoice(targets)
    controller.submitvoting(name, targets(choice))

  def witchAction(name: String, targets: Vector[String]): Unit =
    println(
      s"$name, möchtest du in dieser Nacht jemanden heilen oder töten? (tippe 'Yes' für Action)"
    )
    targets.zipWithIndex.foreach { case (t, i) =>
      println(s"$i: $t")
    }
    val choice1 = readLine().trim
    if choice1.equalsIgnoreCase("yes") then
      val choice = readValidChoice(targets)
      controller.submitNightChoice(name, targets(choice))
    else controller.submitNightChoice(name, name)

  def readValidChoice(targets: Vector[String]): Int =
    val c = readLine().trim
    c.toIntOption match
      case Some(input) =>
        if input >= 0 && input < targets.size then input
        else
          println("Ungültige Wahl")
          readValidChoice(targets)
      case None =>
        println("das ist keine Zahl")
        readValidChoice(targets)

  def amorAction(name: String, targets: Vector[String]) =
    if targets.size >= 2 then
      println(s"$name, welche 2 Glücklichen möchtest du heute Nacht bis zum Tode verbinden?")
      targets.zipWithIndex.foreach { case (t, i) =>
        println(s"$i: $t")
      }

      val choice1 = readValidChoice(targets)
      val choice2 = readValidChoice(targets)
      // controller.submitNightChoiceAmor(name, targets(choice1), targets(choice2))

  def start(): Unit =
    Thread.sleep(1000)
    controller.addObserver(this)
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()
    controller.saveGameState()

    val names = getPlayerNames(getPlayerAmount())
    clearScreen()
    controller.addRoles(names)

    controller.runGame()

  def getPlayerAmount(): Int =
    val input = readLine("Wie viele Spieler? (mind. 2, max. 6): ").trim()
    input.toIntOption match {
      case Some(n) =>
        if n >= 2 && n <= 6 then n
        else
          println("ungültige Spieleranzahl")
          getPlayerAmount()
      case None =>
        println("ungültige Eingabe")
        getPlayerAmount()
    }

  def getPlayerNames(playerAmount: Int): Vector[String] =
    (0 until playerAmount).map { i =>
      readLine(s"Spieler${i + 1} wie heißen sie: ").trim match
        case "" => s"Spieler ${i + 1}"
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
