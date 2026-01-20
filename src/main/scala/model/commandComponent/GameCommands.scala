// src/main/scala/model/commands/GameCommandInterface.scala
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.Game

trait GameCommand {
  def execute(game: Game): Game
  def undo( game: Game): Game
}


