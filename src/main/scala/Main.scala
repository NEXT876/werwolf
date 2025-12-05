// src/main/scala/Main.scala
package de.htwg.werwolf

import model.Game
import controller.GameController
import view.*

@main def main(): Unit =
  val view: GameView = new TUI()
  val game = Game()
  val controller = GameController(game,view)
  
  game.addObserver(controller)
  controller.start()