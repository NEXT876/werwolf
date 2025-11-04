package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class MainSpec extends AnyWordSpec {



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
}