package de.htwg.werwolf.model.strategiesComponent

import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.CommandInterface

trait NightActionStrategy {
  def performAction(player: Player, game: Game)(using ci : CommandInterface): Game
}
