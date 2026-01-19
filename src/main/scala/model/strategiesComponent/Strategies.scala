// de.htwg.werwolf.model.Strategies.scala
package de.htwg.werwolf.model.strategiesComponent

import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.CommandInterface
import de.htwg.werwolf.model.{Game, NightActionStrategy}
import de.htwg.werwolf.model.commandComponent.{KillCommand, ReviveCommand}
import de.htwg.werwolf.model.Roles
import de.htwg.werwolf.model.Faction

case object WerwolfAction extends NightActionStrategy {

  override def canAct(player: Player, game: Game): Boolean =
    player.isAlive && player.faction == Faction._Werwolf

  override def possibleTargets(player: Player, game: Game): Vector[String] =
    game.players.collect {
      case (name, p) if p.isAlive && name != player.name && p.faction == Faction._Villager => name
    }.toVector

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game =
    game.votes.addVote(target, game)
    game

}

case object WitchAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean =
    player.isAlive && player.faction == Faction._Werwolf

  override def possibleTargets(player: Player, game: Game): Vector[String] =
    game.players.collect {
      case (name, p) if name != player.name => name
    }.toVector

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game =
    if game.players(target).isAlive then ci.executeCommand(ReviveCommand(target), game)
    else ci.executeCommand(KillCommand(player.name, target), game)

}

case object AmorAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean =
    player.isAlive && game.day == 0

  override def possibleTargets(player: Player, game: Game): Vector[String] =
    game.players.collect {
      case (name, p) if p.isAlive && name != player.name => name
    }.toVector

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game = ???

}

case object VillagerAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean =
    player.isAlive && player.faction == Faction._Villager

  override def possibleTargets(player: Player, game: Game): Vector[String] = ???

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game = ???

}

case object TerroristAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean =
    false
    // ToDO prüfen ob links und rechts Spieler tot ist

  override def possibleTargets(player: Player, game: Game): Vector[String] = ???

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game = ???

}

case object NoAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean = false

  override def possibleTargets(player: Player, game: Game): Vector[String] = ???

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game = ???

}

case object voteAction extends NightActionStrategy {
  override def canAct(player: Player, game: Game): Boolean =
    player.isAlive

  override def possibleTargets(player: Player, game: Game): Vector[String] = Vector()

  override def execute(player: Player, target: String, game: Game)(using
      ci: CommandInterface
  ): Game =
    game.votes.addVote(target, game)
    game
}
