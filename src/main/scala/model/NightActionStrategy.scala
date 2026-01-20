package de.htwg.werwolf.model

import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.CommandInterface


trait NightActionStrategy:
  def canAct(player: Player, game: Game): Boolean
  def possibleTargets(player: Player, game: Game): Vector[String]
  def execute(player: Player, target: String, game: Game)
             (using ci: CommandInterface): Game
