package model.commandComponent


import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.*
import de.htwg.werwolf.model.gameCoreComponents.Villager
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
    "KillCommand.undo" should {

      "return the same game if the target does not exist or is alive (line 24)" in {
        // leeres Spiel, kein Spieler
        val game = Game(players = Map.empty)
        val cmd = KillCommand("Killer", "Target")

        val result = cmd.undo(game)   // Zeile 24 wird hier ausgeführt

        result shouldBe game
      }
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
    "ReviveCommand.undo" should {

      "return the same game if the target does not exist or is dead (line 47)" in {
        // Spieler existiert, aber tot/lebendig so dass else-Zweig greift
        val alice = Villager("Alice") 
        val game = Game(players = Map("Alice" -> alice))
        val cmd = ReviveCommand("Alice")

        val result = cmd.undo(game)
      }
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
  }
}
