// src/test/scala/de/htwg/werwolf/model/GameSpec.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter

import java.nio.file.Files
import java.nio.file.Paths
import scala.util.Random
import scala.compiletime.uninitialized


class GameSpec extends AnyWordSpec with Matchers with BeforeAndAfter {

  var game: Game = uninitialized
  var observerCalled: GameEvent = uninitialized

  // Mock Observer
  class TestObserver extends Observer[GameEvent] {
    override def update(event: GameEvent): Unit = {
      observerCalled = event
    }
  }

  before {
    game = new Game()
    observerCalled = null
    game.addObserver(new TestObserver())
  }

  "A Game" should {

    "initialize with correct default state" in {
      val state = game.currentState
      state.day shouldBe 1
      state.phase shouldBe Phase.Night
      state.isRunning shouldBe true
      state.alivePlayers shouldBe empty
      state.votes shouldBe Votes()
    }
    

   /* "add players and update currentState correctly" in {
      val players = Map(
        "Alice" -> Werwolf("Alice"),
        "Bob" -> Villager("Bob"),
        "Charlie" -> Witch("Charlie")
      )

      game.addRoles(players)

      val state = game.currentState
      state.alivePlayers should contain theSameElementsAs players
      state.alivePlayers("Alice").isAlive shouldBe true

      // Observer wurde benachrichtigt
      observerCalled shouldBe GameEvent.printGameState
    }*/

    "switch phase correctly and notify observer" in {
      game.switchPhase()
      game.currentState.phase shouldBe Phase.Day
      observerCalled shouldBe GameEvent.phaseSwitch

      game.switchPhase()
      game.currentState.phase shouldBe Phase.Night
      observerCalled shouldBe GameEvent.phaseSwitch
    }

    "end the game and notify observer" in {
      game.GameEnd()
      game.currentState.isRunning shouldBe false
      observerCalled shouldBe GameEvent.gameEnd
    }

  /*  "filter dead players from currentState.alivePlayers" in {
      val aliveWolf = Werwolf("Luna")
      val deadVillager = Villager("Max").copy(isAlive = false)

      game.addPlayers(
        Map(
          "Luna" -> aliveWolf,
          "Max" -> deadVillager
        )
      )

      val state = game.currentState
      state.alivePlayers should contain only ("Luna" -> aliveWolf)
      state.alivePlayers should not contain key("Max")
    }*/

    "increment day only when needed (not in this version)" in {
      // In deiner Version wird `day` nicht erhöht → aber Test zeigt: aktuell bleibt 1
      game.switchPhase()
      game.switchPhase()
      game.currentState.day shouldBe 1
    }
  }

  "NarratorService" should {

    "load JSON from file path using os-lib" in {
      val tempDir = os.pwd / "target" / "test-narrator"

      // 1. Lösche alten Ordner (falls vorhanden)
      if (os.exists(tempDir)) os.remove.all(tempDir)
      os.makeDir.all(tempDir)

      val jsonPath = tempDir / "narrator.json"

      val testJson =
        """{
        "Night": {
          "Start": ["Gute Nacht"],
          "Werwolf": ["Wölfe wachen auf"],
          "Witch": ["Hexe?"],
          "Amor": ["Amor!"]
        }
      }"""

      // 2. Schreibe JSON (overwrite = true)
      os.write.over(jsonPath, testJson)

      // 3. Lade mit NarratorService
      val root = game.NarratorService.loadNarratorJson(jsonPath)

      // 4. Prüfe Inhalt
      root.Night.Start should contain("Gute Nacht")
      root.Night.Werwolf should contain("Wölfe wachen auf")
    }

    "return random text for known roles" in {
      val root = Root(
        Night(
          Start = List("A", "B"),
          Werwolf = List("W1", "W2"),
          Witch = List("H1"),
          Amor = List("L1", "L2")
        )
      )
/*
      // Fester Seed → immer dasselbe Ergebnis
      val fixedRandom = new Random(42)
      given Random = fixedRandom*/

      val text1 = game.NarratorService.randomNarratorText("Start", root)
      val text2 = game.NarratorService.randomNarratorText("Werwolf", root)
      val text3 = game.NarratorService.randomNarratorText("Witch", root)
      val text4 = game.NarratorService.randomNarratorText("Amor", root)

      text1 should (be("A") or be("B"))
      text2 should (be("W1") or be("W2"))
      text3 should be("H1")
      text4 should (be("L1") or be("L2"))

    }
    "return empty string for unknown role" in {
      val root = Root(Night(List(), List(), List(), List()))
      given Random = new Random(0)
      game.NarratorService.randomNarratorText("Seher", root) shouldBe ""
    }
  }
}
