// src/test/scala/de/htwg/werwolf/model/GameSpec.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack

import java.nio.file.Files
import java.nio.file.Paths
import scala.util.Random
import scala.compiletime.uninitialized
import de.htwg.werwolf.util.Observer
import de.htwg.werwolf.model.Roles


class GameSpec extends AnyWordSpec with Matchers with BeforeAndAfter {

  var game: Game = uninitialized
  var observerCalled: GameEvent = uninitialized
    // DummyPlayer für Tests
  case class DummyPlayer(name: String, var isAlive: Boolean, role: Roles) extends Player {
    def _isAlive: Boolean = isAlive

    def die = copy(isAlive = false)
    def revive = copy(isAlive = true)
    def faction: Faction = ???

    def nightAction: NightActionStrategy = NoAction
    def vote(target: Player): String = s"$name votes for ${target.name}"
    def winCondition(players: Map[String, Player]): Boolean = ???
  }

  // Dummy GameEvent Observer
  var lastEvent: Option[GameEvent] = None
  def observer(event: GameEvent): Unit = lastEvent = Some(event)

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
      val state = game
      state.day shouldBe 1
      state.phase shouldBe Phase.Night
      state.isRunning shouldBe true
      state.players shouldBe empty
      state.votes shouldBe Votes()
    }

/* // TODO: fix observer, lauscht nicht dem alten obj, sollte über konstruktor an das neue
      Game obj weitergegeben werden
    "switch phase correctly and notify observer" in {
      val game1 = game.switchPhase()
      game1.phase shouldBe Phase.Day
      observerCalled shouldBe GameEvent.phaseSwitch(Phase.Day)

      val game2 = game1.switchPhase()
      game2.phase shouldBe Phase.Night
      observerCalled shouldBe GameEvent.phaseSwitch(Phase.Night)
    }*/


    "increment day only when needed (not in this version)" in {
      game.switchPhase()
      game.switchPhase()
      game.day shouldBe 1
    }

    "Game.getRoles" should {
      "return correct roles for 2 players" in {
        val game = Game()
        val roles = game.getRoles(2)
        roles should contain(Roles.werwolf)
        roles should contain(Roles.villager)
      }

      "return roles for more than 2 players" in {
        val game = Game()
        val roles = game.getRoles(6)
        roles.count(_ == Roles.werwolf) shouldBe 2
      }
    }

    "Game.addRoles" should {
      "add roles correctly to players" in {
        val game = Game()
        val playerNames = Vector("Alice", "Bob")
        val newGame = game.addRoles(playerNames)

        newGame.players.keys should contain allElementsOf playerNames
        newGame.players.values.map(_.role).toVector should have size playerNames.size
      }
    }

/*
    "Game.executeCommand" should {
      "execute and store commands" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Villager")
        var game = Game(players = Map(killer.name -> killer, target.name -> target))

        val command = KillCommand(killer, target, game)
        game = game.executeCommand(command)

        game.players(target.name).isAlive shouldBe false
        game.commandHistory.nonEmpty shouldBe true
      }
    }

    "Game.undoLast" should {
      "undo the last command" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Villager")
        var game = Game(players = Map(killer.name -> killer, target.name -> target))

        val command = KillCommand(killer, target, game)
        game = game.executeCommand(command)
        game = game.undoLast()

        game.players(target.name).isAlive shouldBe true
      }
    }

    "Game.replay" should {
      "replay commands without error" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Villager")
        var game = Game(players = Map(killer.name -> killer, target.name -> target))

        val command = KillCommand(killer, target, game)
        game = game.executeCommand(command)

        noException should be thrownBy game.replay()
      }
    }*/

    "Game.createMemento / restoreFromMemento" should {
      "create and restore memento" in {
        val killer = DummyPlayer("Werwolf", true, Roles.werwolf)
        val target = DummyPlayer("Opfer", true, Roles.villager)
        var game = Game(players = Map(killer.name -> killer, target.name -> target))

        val memento = game.createMemento()
        val newGame = game.restoreFromMemento(memento)

        newGame.players.keys shouldBe game.players.keys
        newGame.phase shouldBe game.phase
        newGame.day shouldBe game.day
        newGame.isRunning shouldBe game.isRunning
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
      // Fester Seed → immer dasselbe Ergebnis
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
}