// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Observer, GameEvent, Game, NightPhaseStarted, WerewolfTurn}
import de.htwg.werwolf.view.TUI
import de.htwg.werwolf.model.*

class GameController(game: Game) extends Subject {
  override def update(event: GameEvent): Unit = event match {
    case NightPhaseStarted(roles)  => {}
    case WerewolfTurn(name, roles) => {}
  }

  val data =
    game.NarratorService.loadNarratorJson(os.pwd / "src" / "main" / "resources" / "narrator.json")

  val text = game.NarratorService.randomNarratorText("Start", data)

  def getPlayerNames(playerAmount: Int): Vector[String] =
    (0 until playerAmount).foldLeft(Vector.empty[String]) { (acc, i) =>
      var name = notifyRequestName(i)
      while acc.contains(name) do
        notifyDuplicateNameWarning()
        name = notifyRequestName(i)
      acc :+ name
    }

}
