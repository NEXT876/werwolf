// src/test/scala/de/htwg/werwolf/controller/GameControllerSpec.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import de.htwg.werwolf.view.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalactic.TypeCheckedTripleEquals
import scala.util.Random

class GameControllerSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
/*
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
    }

    "process 'switchPhase' command" in {
      val game = Game()
      val view: GameView = new TUI()
      val controller = new GameController(game, view)
      // Spy auf game.switchPhase()
      var called = false
      val spyGame = new Game() {
        override def switchPhase(): Game = {
          called = true
          super.switchPhase()
        }
      }
      val spyController = new GameController(spyGame,view)

      spyController.process("switchPhase")
      called shouldBe true
    }

    "process 'GameEnd' command" in {
      val game = Game()
      val view: GameView = new TUI()
      val controller = new GameController(game, view)
      var ended = false
      val spyGame = new Game() {
        override def GameEnd(): Game = {
          ended = true
          super.GameEnd()
        }
      }
      val spyController = new GameController(spyGame, view)

      spyController.process("GameEnd")
      ended shouldBe true
    }

    "ignore unknown commands" in {
      val view: GameView = new TUI()
      var called = false
      val spyGame = new Game() {
        override def switchPhase(): Game = {
        called = true               // side-effect
        super.switchPhase()         // korrekter Rückgabewert vom Typ Game
        }
      }
      val spyController = new GameController(spyGame, view)
      spyController.process("unknown")
      called shouldBe false
}
*/*/
  }
