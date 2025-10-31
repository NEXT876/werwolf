package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class WerwolfSpec extends AnyWordSpec {

    // Game.scala
    "the addRoles function" should {
        "assign one Werwolf and one villager for two players" in {
            val players = Vector("Alice", "Bob")
            val roles = addRoles(players)
            roles should have size 2
            roles.keys should contain allOf("Alice", "Bob")
            roles("Alice").role should (be("Werwolf") or be("Villager"))
            roles("Bob").role should (be("Werwolf") or be("Villager"))
        }

        "return a map of one Werwolf and 2 random roles for more than 2 and less than 6 players" in {
            val players = Vector("Alice", "Bob", "Karl")
            val roles = addRoles(players)
            roles should have size 3
            roles.values.count(_.role == "Werwolf") should be(1)
            roles.values.map(_.name) should contain allOf("Alice", "Bob", "Karl")
        }

        "return a map of two Werwolf and two random roles for more tahn 5 an less than 8 players" in {
            val players = Vector("Alice", "Bob", "Karl", "Lara", "Paul", "Clara")
            val roles = addRoles(players)
            roles should have size 6
            roles.values.count(_.role == "Werwolf") should be(2)
            roles.values.map(_.name) should contain allOf("Alice", "Bob", "Karl", "Lara", "Paul", "Clara")
        }
    }
    // case class is needed to provide printPlayers with the players Map
    // in order to make an instance of the Player trait is must not be sealed
    case class DummyPlayer(name: String, isAlive: Boolean, role: String) extends Player {
        def vote(target: Player): String = s"$name voted for ${target.name}"
        def die: Player = copy(isAlive = false)
    }

    // TUI.scala
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

    // Main.scala
    "The getplayerAmount function" should {
        "return an Integer of the amount of players" in {
            val playerAmount = getplayerAmount(true, 5)
            playerAmount shouldBe a [Int]
            //playerAmount should be >= 2
            //playerAmount should be <= 7
        }
    }

    "The getPlayerNames function" should {
        "return a Vector containing the playerNames" in {
            val playerAmount = 3
            val playerName = getPlayerNames(true, playerAmount, Vector[String]("Bob", "Alice", "Beat"))
            playerName should have size 3
            playerName shouldBe a [Vector[String]]
        }
    }

    // Roles.java
    "the functions from werwolf" should {
        "return String werwolf, false, vote" in {
            val werwolf = Werwolf("Hans")
            val player = Villager("Berta")
            val result_role = werwolf.role
            val result_die = werwolf.die
            val result_vote = werwolf.vote(player)
            result_role should be("Werwolf")
            result_die.isAlive should be(false)
            result_vote should be("Werwolf Hans votes for Berta to die")
        }
    }

    "the functions from Villager" should {
        "return String Villager, false, vote" in {
            val villager = Villager("Hansi")
            val player = Villager("Bertai")
            val result_role = villager.role
            val result_die = villager.die
            val result_vote = villager.vote(player)
            result_role should be("Villager")
            result_die.isAlive should be(false)
            result_vote should be("Villager Hansi votes for Bertai to die")
        }
    }

    "the functions from Amor" should {
        "return String Amor, false, vote" in {
            val amor = Amor("Herman")
            val player = Villager("Bert")
            val result_role = amor.role
            val result_die = amor.die
            val result_vote = amor.vote(player)
            result_role should be("Amor")
            result_die.isAlive should be(false)
            result_vote should be("Amor Herman votes for Bert to die")
        }
    }

    "the functions from Terrorist" should {
        "return String Terrorist, false, vote" in {
            val terrorist = Terrorist("Hannes")
            val player = Villager("Brta")
            val result_role = terrorist.role
            val result_die = terrorist.die
            val result_vote = terrorist.vote(player)
            result_role should be("Terrorist")
            result_die.isAlive should be(false)
            result_vote should be("Terrorist Hannes votes for Brta to die")
        }
    }

    "the functions from Witch" should {
        "return String Witch, false, vote" in {
            val witch = Witch("Han")
            val player = Villager("Bertas")
            val result_role = witch.role
            val result_die = witch.die
            val result_vote = witch.vote(player)
            result_role should be("Witch")
            result_die.isAlive should be(false)
            result_vote should be("Witch Han votes for Bertas to die")
        }
    }
}
