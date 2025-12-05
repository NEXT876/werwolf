// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import de.htwg.werwolf.view.GameView

import scala.util.Random
import de.htwg.werwolf.util.Observer

class GameController(private var _game: Game, val view: GameView) extends Observer[GameEvent] {
  private var savedMemento: Option[GameMemento] = None
  def game: Game = _game
   def updateGame(newGame: Game): Game =
    _game.removeObserver(this)
    _game = newGame
    _game.addObserver(this)
    game

  private def saveGameState(): Unit =
    savedMemento = Some(game.createMemento())

  def undoFull(): Unit = savedMemento match
    case Some(memento) =>
      val restoredGame = game.restoreFromMemento(memento)
      updateGame(restoredGame)
      view.tiping("↶ Vollständiges Undo – alles zurückgesetzt!", 70)
    case None =>
      view.tiping("Kein gespeicherter Spielstand zum Wiederherstellen.", 70)

  def executeCommand(cmd: GameCommand): Game =
    saveGameState()
    updateGame(game.executeCommand(cmd))

  def undoCommand(): Game =
    saveGameState()
    updateGame(game.undoLast())

  def start(): Unit = {
    view.clearScreen()
    view.tiping("Willkommen zu Werwolf", 100)
    view.showLogo()

    saveGameState()
    val names = view.getPlayerNames(view.getPlayerAmount())

    view.clearScreen()

    saveGameState()
    updateGame(game.addRoles(names))
    runGame()
  }

   def runGame(): Unit =
    while (game.isRunning) {
      view.clearScreen()

      game.phase match {
        case Phase.Night => game.runNightPhase()
        case Phase.Day   => game.runDayPhase()
      }

      game.checkWinCondition(game.players) match {
        case Some(winningFaction) =>
          saveGameState()
          executeCommand(GameEndCommand(Some(winningFaction)))
          view.tiping(s"Die $winningFaction haben gewonnen!!!", 120)
        case None =>
      }
      // undoFull()
    }
    view.showGameOver()

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(players) =>
        view.clearScreen()
        view.showLogo()
        view.printPlayerRoles(players.map { case (name, player) =>
          (name, player.role.toString(), player.isAlive)
        }.toVector)

      case GameEvent.printnarratorText(text) =>
        view.showLogo()
        view.tiping(text)

}
