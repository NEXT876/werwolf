// src/main/scala/Main.scala
package de.htwg.werwolf

import model.Game
import controller.GameController
import view.TUI

@main def main(): Unit =
  val game = Game()
  val controller = GameController(game)
  val tui = TUI(controller)
  game.addObserver(tui)
  tui.start()
