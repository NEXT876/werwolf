// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Observer, GameEvent, Game, NightPhaseStarted, WerewolfTurn}

class GameController(game: Game) extends Observer {
  game.addObserver(this)

  override def update(event: GameEvent): Unit = event match {
    case NightPhaseStarted(roles)  => {}
    case WerewolfTurn(name, roles) => {}
  }

  val data =
    game.NarratorService.loadNarratorJson(os.pwd / "src" / "main" / "resources" / "narrator.json")

  val text = game.NarratorService.randomNarratorText("Start", data)
}
