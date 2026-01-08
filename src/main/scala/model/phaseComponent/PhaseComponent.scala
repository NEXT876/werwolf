package de.htwg.werwolf.model.phaseComponent

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.CommandInterface
import de.htwg.werwolf.model.voteComponent.Votes

enum Phase:
  case Night, Day


case class PhaseComponent()(using ci: CommandInterface) extends PhaseComponentInterface {
  def switchPhase(game : Game): Game =
    ci.createMemento(game)
    val newPhase = if game.phase == Phase.Night then Phase.Day else Phase.Night
    val newDay = game.day + 1
    game.copy(phase = newPhase, day = newDay, votes = Votes())

  def runNightPhase(game : Game)(using ci: CommandInterface): Game =
    val updatedGame = game.players.foldLeft(game) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)
    }
    updatedGame

  def runDayPhase(game : Game)(using ci: CommandInterface): Game =
     val updatedGame = game.players.foldLeft(game) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)
    }
      updatedGame
}