// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller.gameControllerComponent

import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.{CommandInterface, NarratorInterface, GameCoreInterface}
import de.htwg.werwolf.fileIO.IOInterface
import java.nio.file.Paths
import de.htwg.werwolf.model.commandComponent.{
  GameMemento,
  GameCommand,
  GameEndCommand,
  KillCommand
}
import de.htwg.werwolf.model.{Roles, Phase, GameEvent, Game, Faction}

import scala.util.{Try, Success, Failure}
import scala.util.Random
import java.nio.file.Files

class GameController(private var _game: Game)(using
    narrator: NarratorInterface,
    ci: CommandInterface,
    GC: GameCoreInterface,
    io: IOInterface
) extends GameControllerInterface {

  def saveIntoFile(name: String): Unit =
    val memento = ci.createMemento(_game)
    val dir = Paths.get("saves")
    Files.createDirectories(dir)
    val path = dir.resolve(name + io.extension)
    io.write(path, memento)


  def loadFromFile(name: String): Unit =
    val path = Paths.get("saves", name + io.extension)
    val memento = io.read(path)
    _game = ci.restoreFromMemento(memento, _game)

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
    val Players =
      game.players.values
        .filter(_.faction == Faction._Werwolf)
        .toSeq
        .sortBy(p => p.role != Roles.werwolf)

    val updatedGame =
      game.copy(votes = GC.resetVotes(), pendingNightActors = Players.map(_.name).toSet)
    updateGame(updatedGame)

    Players.foreach { player =>
      if player.nightAction.canAct(player, updatedGame) then
        val targets = player.nightAction.possibleTargets(player, updatedGame)
        notifyObservers(
          GameEvent.askForTargetNight(player.name, player.role, targets)
        )
    }

  def runDayPhase(): Unit =
    val player = game.players.values
      .filter(_.isAlive)

    val updatedGame =
      game.copy(votes = GC.resetVotes(), pendingNightActors = player.map(_.name).toSet)
    updateGame(updatedGame)

    player.foreach { player =>
      if player.nightAction.canAct(player, game) then
        val targets = game.players.values.filter(_.name != player.name).map(_.name).toVector
        notifyObservers(
          GameEvent.askForTargetDay(player.name, targets)
        )
    }

  def submitNightChoice(playerName: String, target: String): Unit =
    val player = game.players(playerName)

    val updatedGame =
      if game.phase == Phase.Night then player.nightAction.execute(player, target, game)
      else player.dayAction.execute(player, target, game)
    updateGame(updatedGame)

    checkIfGameEnd(updatedGame.checkWinCondition(game.players))

  def submitvoting(playerName: String, target: String): Unit =
    val player = game.players(playerName)

    val afterActorRemoval =
      game.copy(
        pendingNightActors = game.pendingNightActors - playerName
      )

    val afterAction =
      if game.phase == Phase.Night then
        player.nightAction.execute(player, target, afterActorRemoval)
      else player.dayAction.execute(player, target, afterActorRemoval)

    updateGame(afterAction)

    val remainingWerewolves =
      afterAction.pendingNightActors.exists { name =>
        afterAction.players(name).role == Roles.werwolf
      }

    if !remainingWerewolves then
      afterAction.votes.getVotedPlayer(afterAction) match
        case Some(name) =>
          ci.executeCommand(KillCommand("die werwölfe", name), afterAction)
        case None =>
          println("Niemand wurde gewählt")

      finishNightPhase()

    checkIfGameEnd(
      afterAction.checkWinCondition(afterAction.players)
    )

  def finishNightPhase(): Unit =
    updateGame(switchPhase())
    notifyObservers(GameEvent.switchPhase(game.phase.toString))
    runCurrentPhase()

  def switchPhase(): Game =
    game.copy(phase = if game.phase == Phase.Night then Phase.Day else Phase.Night)

  def runGame(): Unit =
    notifyObservers(GameEvent.InitialthingsDone)
    runCurrentPhase()

  def runCurrentPhase(): Unit =
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
      case Phase.Day =>
        notifyObservers(
          GameEvent.printnarratorText(
            narrator.randomDayNarratorTexte("Start")
          )
        )
        notifyObservers(GameEvent.printGameState(game.players.values.mkString("\n")))
        runDayPhase()

    }

  def checkIfGameEnd(tocheck: Option[Faction]): Unit = tocheck match
    case Some(winningFaction) =>
      saveGameState()
      updateGame(executeCommand(GameEndCommand(Some(winningFaction)), game))

      notifyObservers(GameEvent.printText(s"Die $winningFaction haben gewonnen!!!", 120))
      notifyObservers(GameEvent.GameOver)
    case None =>

}
