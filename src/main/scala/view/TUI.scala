// src/main/scala/view/TUI.scala
package de.htwg.werwolf.view

import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Observer

import scala.io.StdIn.readLine
import java.util.concurrent.LinkedBlockingQueue
import de.htwg.werwolf.model.Roles

class TUI()(using controller: GameControllerInterface) extends Observer[GameEvent] {

  // Events und Tastatureingaben laufen durch eine Queue in EINEN TUI-Thread:
  // update() blockiert so nie den Controller, und die GUI kann parallel eingeben
  private val inbox = new LinkedBlockingQueue[Either[String, GameEvent]]()

  private case class Prompt(name: String, role: Option[Roles], targets: Vector[String])
  private var prompts = Vector.empty[Prompt]
  private var started = false
  private var over = false
  private var amount = 0
  private var names = Vector.empty[String]

  override def update(event: GameEvent): Unit = inbox.put(Right(event))

  private def handleEvent(event: GameEvent): Unit =
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

      case GameEvent.initialThingsDone =>
        started = true // Setup kann auch in der GUI abgeschlossen worden sein

      case GameEvent.switchPhase(phase) =>
        println(s"Phase: $phase")

      case GameEvent.gameOver =>
        over = true
        prompts = Vector.empty
        showGameOver()
        println("Enter drücken, um das Spiel zu beenden")

      case GameEvent.printErrorMSG(msg) =>
        printErrorMsg(msg)

      case GameEvent.askForTargetNight(name, role, targets) =>
        if targets.nonEmpty && (role == Roles.werwolf || role == Roles.witch) then
          addPrompt(Prompt(name, Some(role), targets))

      case GameEvent.askForTargetDay(name, targets) =>
        addPrompt(Prompt(name, None, targets))

      case GameEvent.playerActed(name, target) =>
        println(s"$name hat gewählt: $target")
        val wasCurrent = prompts.headOption.exists(_.name == name)
        prompts = prompts.filterNot(_.name == name)
        if wasCurrent then prompts.headOption.foreach(showPrompt)

      case _ =>

  private def addPrompt(p: Prompt): Unit =
    prompts :+= p
    if prompts.size == 1 then showPrompt(p)

  private def showPrompt(p: Prompt): Unit =
    p.role match
      case Some(Roles.witch) =>
        println(s"${p.name}, möchtest du in dieser Nacht jemanden heilen oder töten? (Zahl für Aktion, Enter für keine)")
      case Some(_) => println(s"${p.name}, welches Opfer reißt du diese Nacht?")
      case None    => println(s"${p.name}, für wen votest du")
    p.targets.zipWithIndex.foreach { case (t, i) => println(s"$i: $t") }

  private def handleLine(line: String): Unit =
    if over then javafx.application.Platform.exit()
    else if !started then setupInput(line)
    else
      prompts.headOption match
        case Some(p) if p.role.contains(Roles.witch) && line.isEmpty =>
          controller.submitNightChoice(p.name, p.name)
        case Some(p) =>
          // Prompt wird erst entfernt, wenn der Controller playerActed sendet
          parseChoice(line, p.targets).foreach { i =>
            if p.role.contains(Roles.witch) then controller.submitNightChoice(p.name, p.targets(i))
            else controller.submitvoting(p.name, p.targets(i))
          }
        case None =>

  def parseChoice(line: String, targets: Vector[String]): Option[Int] =
    line.toIntOption match
      case Some(i) if i >= 0 && i < targets.size => Some(i)
      case Some(_) =>
        println("Ungültige Wahl")
        None
      case None =>
        println("das ist keine Zahl")
        None

  private def setupInput(line: String): Unit =
    if amount == 0 then
      line.toIntOption match
        case Some(n) if n >= 2 && n <= 6 =>
          amount = n
          askName()
        case Some(_) =>
          println("ungültige Spieleranzahl")
          askAmount()
        case None =>
          println("ungültige Eingabe")
          askAmount()
    else
      names :+= (if line.isEmpty then s"Spieler ${names.size + 1}" else line)
      if names.size < amount then askName()
      else
        clearScreen()
        controller.addRoles(names)
        controller.runGame()

  private def askAmount(): Unit = prompt("Wie viele Spieler? (mind. 2, max. 6): ")
  private def askName(): Unit = prompt(s"Spieler${names.size + 1} wie heißen sie: ")
  private def prompt(text: String): Unit =
    print(text)
    System.out.flush()

  def start(): Unit =
    Thread.sleep(1000)
    controller.addObserver(this)
    clearScreen()
    tiping("Willkommen zu Werwolf", 100)
    showLogo()
    controller.saveGameState()
    if !started then askAmount()

    val reader = new Thread(() =>
      Iterator.continually(readLine()).takeWhile(_ != null).foreach(l => inbox.put(Left(l.trim)))
    )
    reader.setDaemon(true)
    reader.start()

    while true do
      inbox.take() match
        case Left(line)   => handleLine(line)
        case Right(event) => handleEvent(event)

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
    print("\u001b[H\u001b[2J")
    System.out.flush()

  def showGameOver(): Unit =
    println("Das Game ist vorbei")

  def printErrorMsg(msg: String): Unit =
    println(msg)
}
