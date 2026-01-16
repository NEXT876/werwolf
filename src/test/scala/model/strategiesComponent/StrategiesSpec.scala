package de.htwg.werwolf.model.strategiesComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.mockito.ArgumentMatchers

import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.{Game, Faction}
import de.htwg.werwolf.model.CommandInterface
import de.htwg.werwolf.model.commandComponent.{KillCommand, reviveCommand}
import de.htwg.werwolf.model.gameCoreComponents.Votes

/** ===== konkrete Test-Implementierungen ===== */

final case class TestPlayer(
  name: String,
  faction: Faction,
  isAlive: Boolean
) extends Player {
  def dayAction: de.htwg.werwolf.model.NightActionStrategy = ???  // Oder echte Impl
  def die: de.htwg.werwolf.model.gameCoreComponents.Player = ???   // z.B. copy(isAlive = false)
  def nightAction: de.htwg.werwolf.model.NightActionStrategy = ???
  def revive: de.htwg.werwolf.model.gameCoreComponents.Player = ???
  def role: de.htwg.werwolf.model.Roles = ???
  def vote(target: de.htwg.werwolf.model.gameCoreComponents.Player): String = ???
}

final class TestVotes extends de.htwg.werwolf.model.gameCoreComponents.Votes:
  var lastVote: Option[String] = None
  override def addVote(target: String, game: Game): Votes = {
    lastVote = Some(target)
    this  // Ermöglicht Chaining wie super
  }
final class TestGame(
  override val players: Map[String, Player],
  override val votes: Votes = new TestVotes,
  override val day: Int = 0
) extends Game

class StrategiesSpec extends AnyWordSpec with Matchers with MockitoSugar:

  given CommandInterface = mock[CommandInterface]

  "WerwolfAction" should {

    "allow acting only for alive werwolf players" in {
      val game = TestGame(Map.empty)

      WerwolfAction.canAct(TestPlayer("w", Faction._Werwolf, true), game) shouldBe true
      WerwolfAction.canAct(TestPlayer("w", Faction._Werwolf, false), game) shouldBe false
      WerwolfAction.canAct(TestPlayer("v", Faction._Villager, true), game) shouldBe false
    }

    "return only alive villagers except itself as targets" in {
      val game = TestGame(
        Map(
          "w"  -> TestPlayer("w", Faction._Werwolf, true),
          "v1" -> TestPlayer("v1", Faction._Villager, true),
          "v2" -> TestPlayer("v2", Faction._Villager, false)
        )
      )

      WerwolfAction.possibleTargets(game.players("w"), game) shouldBe Vector("v1")
    }

    "add a vote and return the same game instance" in {
      val votes = new TestVotes
      val game  = TestGame(Map.empty, votes)

      val result =
        WerwolfAction.execute(TestPlayer("w", Faction._Werwolf, true), "target", game)

      votes.lastVote shouldBe Some("target")
      result shouldBe game
    }
  }

  "WitchAction" should {

    "revive alive target via CommandInterface" in {
      val game = TestGame(
        Map("a" -> TestPlayer("a", Faction._Villager, true))
      )

      WitchAction.execute(TestPlayer("w", Faction._Werwolf, true), "a", game)

      verify(summon[CommandInterface])
        .executeCommand(any[reviveCommand], ArgumentMatchers.eq(game))
    }

    "kill dead target via CommandInterface" in {
      val game = TestGame(
        Map("d" -> TestPlayer("d", Faction._Villager, false))
      )

      WitchAction.execute(TestPlayer("w", Faction._Werwolf, true), "d", game)

      verify(summon[CommandInterface])
        .executeCommand(any[KillCommand], ArgumentMatchers.eq(game))
    }
  }

  "Actions with ???" should {

    "throw NotImplementedError to expose missing logic" in {
      val game   = TestGame(Map("x" -> TestPlayer("x", Faction._Villager, true)))
      val player = TestPlayer("p", Faction._Villager, true)

      intercept[NotImplementedError](AmorAction.execute(player, "x", game))
      intercept[NotImplementedError](VillagerAction.possibleTargets(player, game))
      intercept[NotImplementedError](TerroristAction.execute(player, "x", game))
      intercept[NotImplementedError](NoAction.possibleTargets(player, game))
    }
  }

  "voteAction" should {

    "always allow alive players and record a vote" in {
      val votes = new TestVotes
      val game  = TestGame(Map.empty, votes)

      voteAction.canAct(TestPlayer("p", Faction._Villager, true), game) shouldBe true

      voteAction.execute(TestPlayer("p", Faction._Villager, true), "t", game)

      votes.lastVote shouldBe Some("t")
    }
  }
