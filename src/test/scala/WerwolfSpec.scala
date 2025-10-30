package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class WerwolfSpec extends AnyWordSpec {

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
        "printPlayerRoles should format correctly" in {
            val players = Map(
                "Alice" -> new DummyPlayer("Hexe", true),
                "Bob"   -> new DummyPlayer("Werwolf", false)
            )

            val result = printPlayerRoles(players)

            assert(result.contains("Alice"))
            assert(result.contains("Hexe"))
            assert(result.contains("lebt"))
            assert(result.contains("Bob"))
            assert(result.contains("Werwolf"))
            assert(result.contains("tot"))
  }
    }
/*  "The getplayerAmount function" should {
        "return an Integer of the amount of players" in {
            val playerAmount = getplayerAmount()
            playerAmount shouldBe a [Int]
            //playerAmount should be >= 2
            //playerAmount should be <= 7
        }
    }*/

/*  "The getPlayerNames function" should {
        "return a Vector containing the playerNames" in {
            val playerAmount = 3
            val playerName = getPlayerNames(playerAmount)
            playerName should have size 3
            playerName shouldBe a [Vector[String]]
        }
    }*/


}
