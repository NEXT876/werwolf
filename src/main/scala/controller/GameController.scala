// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import de.htwg.werwolf.model.commands.CommandInterface
import de.htwg.werwolf.narrator.Narrator
import de.htwg.werwolf.util.Subject
import scala.util.{Try, Success, Failure}
import scala.util.Random
import de.htwg.werwolf.model.roleUtils.PlayerInterface
import de.htwg.werwolf.model.commands.{GameCommand, GameEndCommand}

class GameController(private var _game: Game)(using
    narrator: Narrator,
    roleAd: PlayerInterface,
    ci: CommandInterface
) extends Subject[GameEvent] {
  private var savedMemento: Option[GameMemento] = None
  def game: Game = _game
  def updateGame(newGame: Game): Game =
    _game = newGame
    game

  def saveGameState(): Unit =
    savedMemento = Some(game.createMemento())

  def undoFull(): Unit = savedMemento match
    case Some(memento) =>
      val restoredGame = game.restoreFromMemento(memento)
      updateGame(restoredGame)
      notifyObservers(GameEvent.printText("↶ Vollständiges Undo – alles zurückgesetzt!", 70))
    case None =>
      notifyObservers(
        GameEvent.printText("Kein gespeicherter Spielstand zum Wiederherstellen.", 70)
      )

  def executeCommand(cmd: GameCommand): Game =
    saveGameState()
    updateGame(ci.executeCommand(cmd, game))

  def undoCommand(): Game =
    saveGameState()
    ci.undoLast(game) match {
      case Success(newGame) =>
        updateGame(newGame)
        newGame
      case Failure(_) =>
        notifyObservers(GameEvent.printErrorMSG("Nichts zum Rückgängigmachen!"))
        game
    }

  def countAlivePlayer(): (Int, Int) =
    val alivePlayers = game.players.values.filter(_.isAlive)
    (
      alivePlayers.count(_.role == roleUtils.Roles.werwolf),
      alivePlayers.count(_.role != roleUtils.Roles.werwolf)
    )

  def addRoles(names: Vector[String]): Unit =
    updateGame(roleAd.addRoles(names, game))

  def runGame(): Unit =
    notifyObservers(GameEvent.InitialthingsDone)
    while (game.isRunning) {
      notifyObservers(GameEvent.clearScreen)

      game.phase match {
        case Phase.Night =>
          notifyObservers(
            GameEvent.printnarratorText(
              narrator.randomNarratorText("Start")
            )
          )
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
          updateGame(game.runNightPhase())
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
        case Phase.Day =>
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
          updateGame(game.runNightPhase())
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
      }
      notifyObservers(GameEvent.switchPhase(game.switchPhase().phase.toString()))

      game.checkWinCondition(game.players) match {
        case Some(winningFaction) =>
          saveGameState()
          executeCommand(GameEndCommand(Some(winningFaction)))
          notifyObservers(GameEvent.printText(s"Die $winningFaction haben gewonnen!!!", 120))
        case None =>
      }

    }
    notifyObservers(GameEvent.GameOver)

}
