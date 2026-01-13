// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller.gameControllerComponent

import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.{CommandInterface, NarratorInterface, GameCoreInterface}
import de.htwg.werwolf.model.commandComponent.{GameMemento, GameCommand}
import de.htwg.werwolf.model.{Roles, Phase, GameEvent, Game, Faction}

import scala.util.{Try, Success, Failure}
import scala.util.Random
import de.htwg.werwolf.view.GUI.update

class GameController(private var _game: Game)(using
    narrator: NarratorInterface,
    ci: CommandInterface,
    GC: GameCoreInterface
) extends GameControllerInterface {
  private var savedMemento: Option[GameMemento] = None
  def game: Game = _game
  def updateGame(newGame: Game): Game =
    _game = newGame
    game

  def saveGameState(): Unit =
    savedMemento = Some(ci.createMemento(game))

  def undoFull(): Unit = savedMemento match
    case Some(memento) =>
      val restoredGame = ci.restoreFromMemento(memento, game)
      updateGame(restoredGame)
      notifyObservers(GameEvent.printText("↶ Vollständiges Undo – alles zurückgesetzt!", 70))
    case None =>
      notifyObservers(
        GameEvent.printText("Kein gespeicherter Spielstand zum Wiederherstellen.", 70)
      )
  def executeCommand(cmd: String, UEP: Option[Faction], game: Game): Game =
    saveGameState()
    updateGame(ci.executeCommand(cmd, UEP, null, null, game))

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
      alivePlayers.count(_.role == Roles.werwolf),
      alivePlayers.count(_.role != Roles.werwolf)
    )

  def addRoles(names: Vector[String]): Unit =
    updateGame(GC.addRoles(names, game))

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
          updateGame(GC.runNightPhase(game))
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
        case Phase.Day =>
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
          updateGame(GC.runNightPhase(game))
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
      }
      notifyObservers(GameEvent.switchPhase(GC.switchPhase(game).phase.toString()))

      game.checkWinCondition(game.players) match {
        case Some(winningFaction) =>
          saveGameState()
          updateGame(executeCommand("GameEndCommand", Some(winningFaction), game))
          // executeCommand(GameEndCommand(Some(winningFaction)), game)
          notifyObservers(GameEvent.printText(s"Die $winningFaction haben gewonnen!!!", 120))
        case None =>
      }

    }
    notifyObservers(GameEvent.GameOver)

}
