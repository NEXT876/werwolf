// de.htwg.werwolf.model.Strategies.scala
package de.htwg.werwolf.model.strategiesComponent

import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.commandComponent.{CommandInterface}
import de.htwg.werwolf.controller.gameControllerComponent.KillCommand
import de.htwg.werwolf.model.Game

case object WerwolfAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} (Werwolf) darf jetzt töten...")
    val possibleTargets = game.players.collect {
      case (name, p) if p.isAlive && name != player.name => name
    }.toVector
    if possibleTargets.isEmpty then game
    else
      val targetName = possibleTargets.head
      val cmd = KillCommand(player.name, targetName)
      ci.executeCommand(cmd, game)
  }
}

case object WitchAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} (Hexe) darf heilen oder vergiften...")
    game
  }
}

case object TerroristAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} (Terorist) darf jetzt explodieren...")
    game
  }
}

case object AmorAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} (Amor) darf jetzt verlieben...")
    game
  }
}

case object VillagerAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} (Villager) darf jetzt leben...")
    game
  }
}

case object NoAction extends NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci: CommandInterface): Game = {
    println(s"${player.name} hat heute Nacht nichts zu tun.")
    game
  }
}