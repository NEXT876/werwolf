// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import de.htwg.werwolf.view.GameView

import scala.util.Random

class GameController(private var _game: Game, val view: GameView) extends Observer[GameEvent] {
  def game: Game = _game
  private def updateGame(newGame: Game): Unit =
    _game.removeObserver(this)
    _game = newGame
    _game.addObserver(this)

  def start(): Unit = {
    view.clearScreen()
    view.tiping("Willkommen zu Werwolf", 100)
    view.showLogo()

    val names = view.getPlayerNames(view.getPlayerAmount())

    view.clearScreen()
    updateGame(game.addRoles(names))
    run()
  }

  def process(input: String): Unit =
    input match
      case "switchPhase" => updateGame(game.switchPhase())
      case "runPhase"    => game.runPhase()
      case "GameEnd"     => updateGame(_game.GameEnd())
      case _             =>

  def run(): Unit =
    while (game.isRunning) {
      process("runPhase")
      process("switchPhase")
      //
      process("GameEnd")
    }

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(alivePlayers) =>
        view.clearScreen()
        view.showLogo()
        view.printPlayerRoles(alivePlayers.map { case (name, player) =>
          (name, player.role, player.isAlive)
        }.toVector)

      case GameEvent.phaseSwitch(phase) =>
      //
      case GameEvent.gameEnd(isRunning) =>
        println("Das Spiel ist vorbei")

}
