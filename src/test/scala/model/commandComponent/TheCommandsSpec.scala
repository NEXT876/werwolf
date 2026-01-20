package model.commandComponent


import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.*
import de.htwg.werwolf.DummyPlayer
import de.htwg.werwolf.model.commandComponent.{KillCommand,ReviveCommand,GameEndCommand}

class KillCommandSpec extends AnyWordSpec with Matchers {

  "KillCommand" should {

    "kill a living player" in {
      val victim = DummyPlayer(Roles.villager, "Hans")
      val game = Game(
        players = Map("Hans" -> victim),
        isRunning = true
      )

      val cmd = KillCommand("Wolfgang", "Hans")
      val updatedGame = cmd.execute(game)

      updatedGame.players("Hans").isAlive shouldBe false
    }

    "do nothing if target does not exist" in {
      val game = Game(players = Map.empty, isRunning = true)

      val cmd = KillCommand("Wolfgang", "Hans")
      val updatedGame = cmd.execute(game)

      updatedGame shouldBe game
    }

    "do nothing if target is already dead" in {
      val deadVictim = DummyPlayer(Roles.villager, "Hans").die
      val game = Game(
        players = Map("Hans" -> deadVictim),
        isRunning = true
      )

      val cmd = KillCommand("Wolfgang", "Hans")
      val updatedGame = cmd.execute(game)

      updatedGame.players("Hans").isAlive shouldBe false
    }

    "undo should revive a killed player" in {
      val victim = DummyPlayer(Roles.villager, "Hans")
      val game = Game(
        players = Map("Hans" -> victim),
        isRunning = true
      )

      val cmd = KillCommand("Wolfgang", "Hans")
      val killedGame = cmd.execute(game)
      val undoneGame = cmd.undo(killedGame)

      undoneGame.players("Hans").isAlive shouldBe true
    }
  }
}

class ReviveCommandSpec extends AnyWordSpec with Matchers {

  "reviveCommand" should {

    "revive a dead player" in {
      val deadPlayer = DummyPlayer(Roles.villager, "Hans").die
      val game = Game(
        players = Map("Hans" -> deadPlayer),
        isRunning = true
      )

      val cmd = ReviveCommand("Hans")
      val updatedGame = cmd.execute(game)

      updatedGame.players("Hans").isAlive shouldBe true
    }

    "do nothing if player is already alive" in {
      val alivePlayer = DummyPlayer(Roles.villager, "Hans")
      val game = Game(
        players = Map("Hans" -> alivePlayer),
        isRunning = true
      )

      val cmd = ReviveCommand("Hans")
      val updatedGame = cmd.execute(game)

      updatedGame.players("Hans").isAlive shouldBe true
    }

    "undo should kill a revived player again" in {
      val deadPlayer = DummyPlayer(Roles.villager, "Hans").die
      val game = Game(
        players = Map("Hans" -> deadPlayer),
        isRunning = true
      )

      val cmd = ReviveCommand("Hans")
      val revivedGame = cmd.execute(game)
      val undoneGame = cmd.undo(revivedGame)

      undoneGame.players("Hans").isAlive shouldBe false
    }
  }
}

class GameEndCommandSpec extends AnyWordSpec with Matchers {

  "GameEndCommand" should {

    "stop the game when executed" in {
      val game = Game(players = Map.empty, isRunning = true)

      val cmd = GameEndCommand()
      val updatedGame = cmd.execute(game)

      updatedGame.isRunning shouldBe false
    }

    "restart the game when undone" in {
      val game = Game(players = Map.empty, isRunning = false)

      val cmd = GameEndCommand()
      val updatedGame = cmd.undo(game)

      updatedGame.isRunning shouldBe true
    }

    "have a description with winner if provided" in {
      val cmd = GameEndCommand(Some(Faction._Werwolf))

      cmd.description should include ("Gewinner")
      cmd.description should include ("Werwölfe")
    }

    "have a generic description without winner" in {
      val cmd = GameEndCommand(None)

      cmd.description shouldBe "Spiel beendet (manuell abgebrochen)"
    }
  }
}
