// src/test/scala/de/htwg/werwolf/model/GameSpec.scala
package de.htwg.werwolf.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.werwolf.model.gameCoreComponents.{Player, Phase, Votes, Roles}

class GameSpec extends AnyWordSpec {

  "A Game" should {

    "have correct default values" in {
      val game = Game()

      game.players shouldBe Map.empty
      game.phase shouldBe Phase.Night
      game.day shouldBe 1
      game.votes shouldBe Votes()
      game.isRunning shouldBe true
      game.commandHistory shouldBe Vector.empty
    }

    "return a nice toString with players" in {
      val player1 = Roles.villager.toPlayer("Alice")
      val player2 = Roles.werwolf.toPlayer("Bob")
      val game = Game(players = Map("Alice" -> player1, "Bob" -> player2))

      game.toString() should include("Alice")
      game.toString() should include("Bob")
      game.toString() should endWith("\n")
    }

    "detect no winner when both factions alive" in {
      val aliveVillager = Roles.villager.toPlayer("Villager")
      val aliveWerwolf = Roles.werwolf.toPlayer("Werwolf")
      val players = Map("V" -> aliveVillager, "W" -> aliveWerwolf)

      Game().checkWinCondition(players) shouldBe None
    }

    "detect Villager win when no Werwölfe alive" in {
      val aliveVillager1 = Roles.villager.toPlayer("V1")
      val aliveVillager2 = Roles.villager.toPlayer("V2")
      val deadWerwolf = Roles.werwolf.toPlayer("W")
      val players = Map("V1" -> aliveVillager1, "V2" -> aliveVillager2, "W" -> deadWerwolf)

      Game().checkWinCondition(players) shouldBe Some(Faction._Villager)
    }

    "detect Werwolf win when only Werwölfe alive" in {
      val aliveWerwolf1 = Roles.werwolf.toPlayer("W1")
      val aliveWerwolf2 = Roles.werwolf.toPlayer("W2")
      val deadVillager = Roles.villager.toPlayer("V")
      val players = Map("W1" -> aliveWerwolf1, "W2" -> aliveWerwolf2, "V" -> deadVillager)

      Game().checkWinCondition(players) shouldBe Some(Faction._Werwolf)
    }

    "handle empty players (no winner)" in {
      Game().checkWinCondition(Map.empty) shouldBe None
    }
  }
}