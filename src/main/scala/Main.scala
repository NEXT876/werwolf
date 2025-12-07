// src/main/scala/Main.scala
package de.htwg.werwolf

import model.Game
import controller.GameController
import view.*

@main def main(): Unit =
  val game = Game()
  val controller = GameController(game)
  val view = TUI(controller)
  
  controller.addObserver(view)
  controller.start()