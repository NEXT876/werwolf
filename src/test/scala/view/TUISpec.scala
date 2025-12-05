// src/test/scala/de/htwg/werwolf/view/TUISpec.scala
package de.htwg.werwolf.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.Player

class TUISpec extends AnyWordSpec with Matchers {
  // case class is needed to provide printPlayers with the players Map
  // in order to make an instance of the Player trait is must not be sealed
  /*
  case class DummyPlayer(name: String, isAlive: Boolean, role: String) extends Player {
    def vote(target: Player): String = s"$name voted for ${target.name}"
    def die: Player = copy(isAlive = false)
  }*/

/**
  "the printPlayerRoles function" should {
    "format player names, roles and states correctly" in {
      val tui = new TUI()

      val players: Vector[AnyRef] = Vector(
        ("Alice", "Hexe", true),
        ("Bob", "Werwolf", false),
        ("Clara", "Seherin", true)
      )


      val result = tui.printPlayerRoles(players)

      result should include("Alice")
      result should include("Hexe")
      result should include(true)

      result should include("Bob")
      result should include("Werwolf")
      result should include(false)

      result.linesIterator.size should be >= 3
      result should startWith("\n================")
      result should endWith("==========================\n")
    }
  }*/
}
