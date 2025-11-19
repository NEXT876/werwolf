// src/test/scala/de/htwg/werwolf/controller/GameControllerSpec.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalactic.TypeCheckedTripleEquals
import scala.util.Random

class GameControllerSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {

  "GameController" should {

    "assign roles correctly for 2 players" in {
      val game = Game() // leeres Spiel
      val controller = new GameController(game)
      val players = Vector("Alice", "Bob")

      controller.initializePlayers(players)

      val assigned = controller.getGame.currentState.alivePlayers.values.toVector
      assigned.map(_.role) should contain theSameElementsAs Vector("Werwolf", "Villager")
      assigned.map(_.name) should contain theSameElementsAs players
    }

    "assign ~1/3 werewolves for larger groups" in {
      val game = Game()
      val controller = new GameController(game)
      val players = Vector("P1", "P2", "P3", "P4", "P5", "P6") // 6 Spieler

      controller.initializePlayers(players)

      val wolves = game.currentState.alivePlayers.values.count(_.role == "Werwolf")
      wolves shouldBe 2 // 6 / 3 = 2
    }

 /*   "handle special roles correctly" in {
      val game = Game()
      val controller = new GameController(game)
      val players = Vector.tabulate(6)(i => s"P$i") // 10 Spieler

      // Fixiere Zufall für reproduzierbare Tests
      val fixedRandom = new Random(42)
      val fixedController = new GameController(game) {
        override def getRoles(playeramount: Int): Vector[Roles] = {
          if (playeramount == 2) Vector(Roles.werwolf, Roles.villager)
          else {
            val wolves = Vector.fill(playeramount / 3)(Roles.werwolf)
            val specials = Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
            wolves ++ fixedRandom.shuffle(specials.take(playeramount - wolves.size))
          }
        }
      }

      fixedController.initializePlayers(players)

      val roles = game.currentState.alivePlayers.values.map(_.role).toSet
      roles should contain("Witch")
      roles should contain("Amor")
      roles should contain("Terrorist")
    }*/

    "process 'switchPhase' command" in {
      val game = Game()
      val controller = new GameController(game)

      // Spy auf game.switchPhase()
      var called = false
      val spyGame = new Game() {
        override def switchPhase(): Unit = {
          called = true
          super.switchPhase()
        }
      }
      val spyController = new GameController(spyGame)

      spyController.process("switchPhase")
      called shouldBe true
    }

    "process 'GameEnd' command" in {
      val game = Game()
      val controller = new GameController(game)

      var ended = false
      val spyGame = new Game() {
        override def GameEnd(): Unit = {
          ended = true
          super.GameEnd()
        }
      }
      val spyController = new GameController(spyGame)

      spyController.process("GameEnd")
      ended shouldBe true
    }

    "ignore unknown commands" in {
      val game = Game()
      val controller = new GameController(game)

      var called = false
      val spyGame = new Game() {
        override def switchPhase(): Unit = called = true
      }
      val spyController = new GameController(spyGame)

      spyController.process("unknown")
      called shouldBe false
    }
  }
}