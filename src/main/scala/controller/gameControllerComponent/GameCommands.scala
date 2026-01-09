// src/main/scala/model/commands/GameCommandInterface.scala
package de.htwg.werwolf.controller.gameControllerComponent

import de.htwg.werwolf.model.Game

trait GameCommand {
  def description: String
  def execute(game: Game): Game
  def undo( game: Game): Game
}