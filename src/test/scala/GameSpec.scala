package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.werwolf.model.{addRoles, night}
import de.htwg.werwolf.model.{Villager, Werwolf}

class GameSpec extends AnyWordSpec {

  "the addRoles function" should {
    "assign one Werwolf and one villager for two players" in {
      val players = Vector("Alice", "Bob")
      val roles = addRoles(players)
      roles should have size 2
      roles.keys should contain allOf ("Alice", "Bob")
      roles("Alice").role should (be("Werwolf") or be("Villager"))
      roles("Bob").role should (be("Werwolf") or be("Villager"))
    }

    "return a map of one Werwolf and 2 random roles for more than 2 and less than 6 players" in {
      val players = Vector("Alice", "Bob", "Karl")
      val roles = addRoles(players)
      roles should have size 3
      roles.values.count(_.role == "Werwolf") should be(1)
      roles.values.map(_.name) should contain allOf ("Alice", "Bob", "Karl")
    }

    "return a map of two Werwolf and two random roles for more tahn 5 an less than 8 players" in {
      val players = Vector("Alice", "Bob", "Karl", "Lara", "Paul", "Clara")
      val roles = addRoles(players)
      roles should have size 6
      roles.values.count(_.role == "Werwolf") should be(2)
      roles.values.map(
        _.name
      ) should contain allOf ("Alice", "Bob", "Karl", "Lara", "Paul", "Clara")
    }
  }

  "the night function" should {
    "return a map of players with one or less additional deaths" in {
      val players = Map(
        "Bob" -> Villager("Bob"),
        "Ben" -> Villager("Ben"),
        "Berta" -> Werwolf("Berta")
      )
      val result_1 = night(players, 1)
      val result_2 = night(players, 0)
      result_1 should be(
        Map(
          "Bob" -> { Villager("Bob", Villager("Bob").isAlive == true) },
          "Ben" -> { Villager("Ben", Villager("Ben").isAlive == true) },
          "Berta" -> { Werwolf("Berta", Werwolf("Berta").isAlive == false) }
        )
      )
      result_2 should be(
        Map(
          "Bob" -> { Villager("Bob", Villager("Bob").isAlive == true) },
          "Ben" -> { Villager("Ben", Villager("Ben").isAlive == true) },
          "Berta" -> { Werwolf("Berta", Villager("Berta").isAlive == false) }
        )
      )
    }
  }
}
