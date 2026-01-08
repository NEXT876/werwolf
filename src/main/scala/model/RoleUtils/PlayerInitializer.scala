package de.htwg.werwolf.model.RoleUtils
import de.htwg.werwolf.model.Game
trait PlayerInitializer:
  def addRoles(playerNames: Vector[String], game: Game): Game
