package de.htwg.werwolf.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.playerComponent.Roles
import de.htwg.werwolf.model.playerComponent.Player
import de.htwg.werwolf.model.strategiesComponent.NightActionStrategy
import de.htwg.werwolf.model.Faction
import de.htwg.werwolf.model.Game

class CommandSpec extends AnyWordSpec with Matchers {

  // Dummy Player für Tests (Player ist ein Trait)
  class TestPlayer(val name: String, var isAlive: Boolean = true, val faction: Faction) extends Player {
    override def role: Roles = ???
    override def vote(target: Player): String = ???
    override def nightAction: NightActionStrategy = ???
    override def die: Player = { isAlive = false; this }
    override def revive: Player = { isAlive = true; this }
  }

  "KillCommand" should {

    "return correct description" in {
      val cmd = KillCommand("A", "B")
      cmd.description shouldBe "A tötet B" // Zeile 12
    }

    "kill an alive target on execute" in {
      val killer = new TestPlayer("A", isAlive = true, faction = Faction._Villager)
      val target = new TestPlayer("B", isAlive = true, faction = Faction._Villager)
      val game = Game(players = Map("A" -> killer, "B" -> target), isRunning = true)

      val cmd = KillCommand("A", "B")
      val updated = cmd.execute(game)

      updated.players("B").isAlive shouldBe false
    }

    "do nothing if target is dead or non-existent" in {
      val killer = new TestPlayer("A", isAlive = true, faction = Faction._Villager)
      val deadTarget = new TestPlayer("B", isAlive = false, faction = Faction._Villager)
      val game = Game(players = Map("A" -> killer, "B" -> deadTarget), isRunning = true)

      val cmd = KillCommand("A", "B")
      val updated = cmd.execute(game)

      updated shouldBe game // Zeile 18
    }

    "undo revives a previously killed target" in {
      val killer = new TestPlayer("A", isAlive = true, faction = Faction._Villager)
      val target = new TestPlayer("B", isAlive = false, faction = Faction._Villager)
      val game = Game(players = Map("A" -> killer, "B" -> target), isRunning = true)

      val cmd = KillCommand("A", "B")
      val reverted = cmd.undo(game)

      reverted.players("B").isAlive shouldBe true // Zeile 27
    }

    "undo does nothing if target is already alive" in {
      val killer = new TestPlayer("A", isAlive = true, faction = Faction._Villager)
      val target = new TestPlayer("B", isAlive = true, faction = Faction._Villager)
      val game = Game(players = Map("A" -> killer, "B" -> target), isRunning = true)

      val cmd = KillCommand("A", "B")
      val reverted = cmd.undo(game)

      reverted shouldBe game
    }
  }

  "GameEndCommand" should {

    "return correct description when winner is Some" in {
      val cmd = GameEndCommand(Some(Faction._Werwolf))
      cmd.description shouldBe "Spiel beendet – Gewinner: Werwölfe" // Zeile 34-35
    }

    "return correct description when winner is None" in {
      val cmd = GameEndCommand(None)
      cmd.description shouldBe "Spiel beendet (manuell abgebrochen)" // Zeile 36
    }

    "execute sets game to not running" in {
      val game = Game(players = Map.empty, isRunning = true)
      val cmd = GameEndCommand()
      val updated = cmd.execute(game)
      updated.isRunning shouldBe false // Zeile 40
    }

    "undo sets game back to running" in {
      val game = Game(players = Map.empty, isRunning = false)
      val cmd = GameEndCommand()
      val reverted = cmd.undo(game)
      reverted.isRunning shouldBe true // Zeile 44
    }
  }
}
