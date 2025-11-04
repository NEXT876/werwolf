package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class GameSpec extends AnyWordSpec {



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
}