// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Observer, GameEvent, Game}

class GameController(game: Game) extends Observer {
  game.addObserver(this)

  val data =
    game.NarratorService.loadNarratorJson(os.pwd / "src" / "main" / "resources" / "narrator.json")

  val text = game.NarratorService.randomNarratorText("Start", data)
}
