package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.werwolf.model.*

object DummyNightAction extends NightActionStrategy:
  def performAction(player: Player, game: Game): Game = (game)

class RolesSpec extends AnyWordSpec {
class DummyPlayer(val role: Roles, val name: String = "dummy") extends Player:
  val faction: Faction = Faction._Villager
  val nightAction: NightActionStrategy = DummyNightAction
  private var alive: Boolean = true
  def isAlive: Boolean = alive

  def vote(target: Player): String = ""
  def die: Player =
    val copy = new DummyPlayer(role, name)
    copy.alive = false
    copy

  def revive: Player =
    val copy = new DummyPlayer(role, name)
    copy.alive = true
    copy

  def winCondition(players: Map[String, Player]): Boolean = true

"the functions from werwolf" should {
        "return String werwolf, false, vote" in {
            val werwolf = Werwolf("Hans")
            val player = Villager("Berta")
            val result_role = werwolf.role
            val result_faction = werwolf.faction
            val result_die = werwolf.die
            val result_revive = werwolf.die.revive
            val result_vote = werwolf.vote(player)
            result_role should be(Roles.werwolf)
            result_faction should be(Faction._Werwolf)
            result_die.isAlive should be(false)
            result_revive.isAlive should be(true)
            result_vote should be("Werwolf Hans votes for Berta to die")
            noException should be thrownBy{
                val action = player.nightAction
                action should not be null}
        }
    }

    "the functions from Villager" should {
        "return String Villager, false, vote" in {
            val villager = Villager("Hansi")
            val player = Villager("Bertai")
            val result_role = villager.role
            val result_faction = villager.faction
            val result_die = villager.die
            val result_revive = villager.die.revive
            val result_vote = villager.vote(player)
            result_role should be(Roles.villager)
            result_faction should be(Faction._Villager)
            result_die.isAlive should be(false)
            result_revive.isAlive should be(true)
            result_vote should be("Villager Hansi votes for Bertai to die")
            noException should be thrownBy{
                val action = player.nightAction
                action should not be null}
        }
    }

    "the functions from Amor" should {
        "return String Amor, false, vote" in {
            val amor = Amor("Herman")
            val player = Villager("Bert")
            val result_role = amor.role
            val result_faction = amor.faction
            val result_die = amor.die
            val result_revive = amor.die.revive
            val result_vote = amor.vote(player)
            result_role should be(Roles.amor)
            result_faction should be(Faction._Villager)
            result_die.isAlive should be(false)
            result_revive.isAlive should be(true)
            result_vote should be("Amor Herman votes for Bert to die")
            noException should be thrownBy{
                val action = player.nightAction
                action should not be null}
        }
    }

    "the functions from Terrorist" should {
        "return String Terrorist, false, vote" in {
            val terrorist = Terrorist("Hannes")
            val player = Villager("Brta")
            val result_role = terrorist.role
            val result_faction = terrorist.faction
            val result_die = terrorist.die
            val result_revive = terrorist.die.revive
            val result_vote = terrorist.vote(player)
            result_faction should be(Faction._Villager)
            result_role should be(Roles.terrorist)
            result_die.isAlive should be(false)
            result_revive.isAlive should be(true)
            result_vote should be("Terrorist Hannes votes for Brta to die")
            noException should be thrownBy{
                val action = player.nightAction
                action should not be null}
        }
    }

    "the functions from Witch" should {
        "return String Witch, false, vote" in {
            val witch = Witch("Han")
            val player = Villager("Bertas")
            val result_role = witch.role
            val result_faction = witch.faction
            val result_die = witch.die
            val result_revive = witch.die.revive
            val result_vote = witch.vote(player)
            result_faction should be(Faction._Villager)
            result_role should be(Roles.witch)
            result_die.isAlive should be(false)
            result_revive.isAlive should be(true)
            result_vote should be("Witch Han votes for Bertas to die")
            noException should be thrownBy{
                val action = player.nightAction
                action should not be null}
        }
    }

    "DoubleVoteDecorator" should {

      "delegate name, role, faction and isAlive to inner player" in {
        val dummy = DummyPlayer(Roles.villager, "Bart")
        val decorated = new DoubleVoteDecorator(dummy)

        decorated.name shouldBe dummy.name
        decorated.isAlive shouldBe dummy.isAlive
        decorated.role shouldBe dummy.role
        decorated.faction shouldBe dummy.faction
      }

      "override vote to vote twice" in {
        val dummy = new DummyPlayer(Roles.villager, "Alice")
        val target = new DummyPlayer(Roles.villager, "Bob")
        val decorated = new DoubleVoteDecorator(dummy)
        decorated.vote(target) shouldBe "Alice votes TWICE for Bob!"
      }

      "die returns a new decorated player with dead inner player" in {
        val dummy = new DummyPlayer(Roles.villager, "V1")
        val decorated = new DoubleVoteDecorator(dummy)

        val deadDecorated = decorated.die
        deadDecorated.isAlive shouldBe false
      }

      "revive returns a new decorated player with alive inner player" in {
        val dummy = new DummyPlayer(Roles.villager, "V2")
        val decorated = new DoubleVoteDecorator(dummy).die

        val revivedDecorated = decorated.revive
        revivedDecorated.isAlive should be(true)
      }
  }
}