package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.{Game,Phase,CommandInterface,Roles}
import de.htwg.werwolf.model.gameCoreComponents.{Votes,GameCore,DoubleVoteDecorator}
import de.htwg.werwolf.model.commandComponent.{GameCommand,ExecuteC,GameMemento}

class VotesSpec extends AnyWordSpec with Matchers {
  "the function addVote" should {
    "return the new votes Map" in {
      val votes0 = Votes()
      val game = Game()

      val votes1 = votes0.addVote("Bob", game)
      votes1.votes should be(Map("Bob" -> 1))

      val votes2 = votes1.addVote("Bob", game)
      votes2.votes should be(Map("Bob" -> 2))

      val votes3 = votes2.addVote("Bob", game)
      votes3.votes should be(Map("Bob" -> 3))

      val votes4 = votes3.addVote("Paula", game)
      votes4.votes should be(Map("Bob" -> 3, "Paula" -> 1))

      val votes5 = votes4.addVote("Paula", game)
      votes5.votes should be(Map("Bob" -> 3, "Paula" -> 2))

      votes5.getVotedPlayer(game) should be(Some("Bob"))
    }
    "return nothing for no votes" in {
      val votes = Votes()

      votes.getVotedPlayer(Game()) should be(None)
    }
  }
}
class GameCoreSpec extends AnyWordSpec with Matchers {

  given CommandInterface = new ExecuteC {
    override def createMemento(game: Game): GameMemento = (
      GameMemento(
        players = game.players,
        phase = game.phase,
        day = game.day,
        votes = game.votes,
        isRunning = game.isRunning,
        commandHistory = game.commandHistory
      )
    )
    override def executeCommand(cmd: GameCommand, game: Game): Game = (game)
  }

  "switchPhase" should {

    "toggle phase, increment day and reset votes" in {
      val core = GameCore()

      val game = Game(
        phase = Phase.Night,
        day = 1,
        votes = Votes(Map("Alice" -> 1)),
        players = Map.empty
      )

      val result = core.switchPhase(game)

      result.phase shouldBe Phase.Day
      result.day shouldBe 2
      result.votes shouldBe Votes()
    }
  }

  "addRoles" should {

    "assign a role to every player and decorate exactly one non-werewolf" in {
      val core = GameCore()
      val names = Vector("A", "B", "C", "D", "E", "F")

      val game = Game(players = Map.empty)

      val result = core.addRoles(names, game)

      result.players.keySet shouldBe names.toSet

      val decorated =
        result.players.values.count(_.isInstanceOf[DoubleVoteDecorator])

      decorated shouldBe 1
    }
  }

  "getRoles" should {

    "return two roles for two players" in {
      val core = GameCore()

      core.getRoles(2).toSet shouldBe
        Set(Roles.werwolf, Roles.villager)
    }

    "return correct number of roles for more players" in {
      val core = GameCore()

      val roles = core.getRoles(6)

      roles.size shouldBe 6
      roles.count(_ == Roles.werwolf) shouldBe 2
    }
  }

  "resetVotes" should {

    "return empty Votes" in {
      val core = GameCore()

      core.resetVotes() shouldBe Votes()
    }
  }
}