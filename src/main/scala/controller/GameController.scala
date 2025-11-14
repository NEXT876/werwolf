// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Game, GameEvent, Observer}

class GameController(val game: Game):
//...
//..
//.

  val data =
    NarratorService.loadNarratorJson(os.pwd / "src" / "main" / "resources" / "narrator.json")

  val text = NarratorService.randomNarratorText("Start", data)
