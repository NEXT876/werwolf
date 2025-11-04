package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class TUISpec extends AnyWordSpec {

    // case class is needed to provide printPlayers with the players Map
    // in order to make an instance of the Player trait is must not be sealed
    case class DummyPlayer(name: String, isAlive: Boolean, role: String) extends Player {
        def vote(target: Player): String = s"$name voted for ${target.name}"
        def die: Player = copy(isAlive = false)
    }

    "the printPlayerRoles function" should {
        "format player names, roles and states correctly" in {
            val players = Map(
                "Alice" -> DummyPlayer("Alice", true, "Hexe"),
                "Bob"   -> DummyPlayer("Bob", false, "Werwolf")
            )

            val result = printPlayerRoles(players)

            result should include ("Alice")
            result should include ("Hexe")
            result should include ("lebt")

            result should include ("Bob")
            result should include ("Werwolf")
            result should include ("tot")

            result.linesIterator.size should be >= 3
            result should startWith ("\n================")
            result should endWith ("==========================\n")
        }
    }
}