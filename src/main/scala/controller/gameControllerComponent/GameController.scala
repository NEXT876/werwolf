// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller.gameControllerComponent

import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.{CommandInterface, NarratorInterface, GameCoreInterface}
import de.htwg.werwolf.model.commandComponent.{GameMemento, GameCommand, GameEndCommand}
import de.htwg.werwolf.model.{Roles, Phase, GameEvent, Game, Faction}

import scala.util.{Try, Success, Failure}
import scala.util.Random
import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.view.GUI.update
import de.htwg.werwolf.model.commandComponent.KillCommand

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
  def executeCommand(cmd: GameCommand, game: Game): Game =
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
      alivePlayers.count(_.role == Roles.werwolf),
      alivePlayers.count(_.role != Roles.werwolf)
    )

  def addRoles(names: Vector[String]): Unit =
    updateGame(GC.addRoles(names, game))

  def runNightPhase(): Unit =
    val werwolves = game.players.values
      .filter(_.faction == Faction._Werwolf)
      .filter(_.role == Roles.werwolf)

    val nonWerwolves = game.players.values
      .filter(_.faction == Faction._Werwolf)
      .filter(_.role != Roles.werwolf)

    updateGame(game.copy(votes = GC.resetVotes()))

    werwolves.foreach { player =>
      if player.nightAction.canAct(player, game) then
        val targets = player.nightAction.possibleTargets(player, game)
        notifyObservers(
          GameEvent.askForTarget(player.name, player.role, targets)
        )
    }
    game.votes.getVotedPlayer(game) match
      case Some(name) =>
        ci.executeCommand(
          KillCommand("die werwölfe", name),
          game
        )
      case None =>
        println("Niemand wurde gewählt")

    nonWerwolves.foreach { player =>
      if player.nightAction.canAct(player, game) then
        val targets = player.nightAction.possibleTargets(player, game)
        notifyObservers(
          GameEvent.askForTarget(player.name, player.role, targets)
        )
    }

  def submitNightChoice(playerName: String, target: String): Unit =
    
    val player = game.players(playerName)
    
    updateGame(
      player.nightAction.execute(player, target, game)
    )

  def runGame(): Unit =
    notifyObservers(GameEvent.InitialthingsDone)
    while (game.isRunning) {
      notifyObservers(GameEvent.clearScreen)

      game.phase match {
        case Phase.Night =>
          notifyObservers(
            GameEvent.printnarratorText(
              narrator.randomNightNarratorTexte("Start")
            )
          )
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
          runNightPhase()
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
        case Phase.Day =>
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
          runNightPhase()
          notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
      }
      notifyObservers(GameEvent.switchPhase(GC.switchPhase(game).phase.toString()))

      game.checkWinCondition(game.players) match {
        case Some(winningFaction) =>
          saveGameState()
          updateGame(executeCommand(GameEndCommand(Some(winningFaction)), game))

          notifyObservers(GameEvent.printText(s"Die $winningFaction haben gewonnen!!!", 120))
        case None =>
      }

    }
    notifyObservers(GameEvent.GameOver)

}
