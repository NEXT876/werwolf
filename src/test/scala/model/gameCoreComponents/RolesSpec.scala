package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.gameCoreComponents._
import de.htwg.werwolf.model.{Faction,Roles,NightActionStrategy,Game,CommandInterface}

object DummyNightAction extends NightActionStrategy:

  def canAct(player: Player, game: Game): Boolean = true

  def possibleTargets(player: Player, game: Game): Vector[String] =
    game.players.keys.toVector.filterNot(_ == player.name)

  def execute(player: Player, target: String, game: Game)
             (using ci: CommandInterface): Game =
    game


case class DummyPlayer(val role: Roles, val name: String = "dummy", alive: Boolean = true) extends Player:
  val faction: Faction = if role == Roles.werwolf then Faction._Werwolf else Faction._Villager
  val nightAction: NightActionStrategy = DummyNightAction
  val dayAction: NightActionStrategy = DummyNightAction
  def isAlive: Boolean = alive
  def vote(target: Player): String = ""
  def die: Player = copy(alive = false)
  def revive: Player = copy(alive = true)
  def winCondition(players: Map[String, Player]): Boolean = true

class RolesSpec extends AnyWordSpec with Matchers {

  "Werwolf" should {
    "have correct role, faction, vote and toString" in {
      val werwolf = Werwolf("Hans")
      val target = Villager("Berta")

      werwolf.role shouldBe Roles.werwolf
      werwolf.faction shouldBe Faction._Werwolf
      werwolf.isAlive shouldBe true
      werwolf.die.isAlive shouldBe false
      werwolf.die.revive.isAlive shouldBe true
      werwolf.vote(target) shouldBe "Werwolf Hans votes for Berta to die"
      //werwolf.toString() shouldBe
      //  "• Hans             | Rolle: Werwolf     | Status: lebt    "

    }
  }

  "Villager" should {
    "have correct role, faction, vote and toString" in {
      val villager = Villager("Hansi")
      val target = Villager("Bertai")

      villager.role shouldBe Roles.villager
      villager.faction shouldBe Faction._Villager
      villager.die.isAlive shouldBe false
      villager.die.revive.isAlive shouldBe true
      villager.vote(target) shouldBe "Villager Hansi votes for Bertai to die"
      //villager.toString() shouldBe
      //   "• Hansi            | Rolle: Villager    | Status: lebt    "
    }
  }

  "Amor" should {
    "have correct role, faction, vote and toString" in {
      val amor = Amor("Herman")
      val target = Villager("Bert")

      amor.role shouldBe Roles.amor
      amor.faction shouldBe Faction._Villager
      amor.vote(target) shouldBe "Amor Herman votes for Bert to die"
      //amor.toString() shouldBe
      //  "• Herman [         | Rolle: Amor      ] | Status: lebt"
    }
  }

  "Terrorist" should {
    "have correct role, faction, vote and toString" in {
      val terrorist = Terrorist("Hannes")
      val target = Villager("Brta")

      terrorist.role shouldBe Roles.terrorist
      terrorist.faction shouldBe Faction._Villager
      terrorist.vote(target) shouldBe
        "Terrorist Hannes votes for Brta to die"
      //terrorist.toString() shouldBe
      //  "• Hannes [         | Rolle: Terrorist ] | Status: lebt"
    }
  }

  "Witch" should {
    "have correct role, faction, vote and toString" in {
      val witch = Witch("Han")
      val target = Villager("Bertas")

      witch.role shouldBe Roles.witch
      witch.faction shouldBe Faction._Villager
      witch.vote(target) shouldBe "Witch Han votes for Bertas to die"
      //witch.toString().trim shouldBe
      //  "• Han | Rolle: Witch | Status: lebt"
    }
  }

  "DoubleVoteDecorator" should {

    val inner = DummyPlayer(Roles.villager, "Alice")
    val target = DummyPlayer(Roles.villager, "Bob")
    val decorated = DoubleVoteDecorator(inner)

    "delegate name, role, faction and isAlive" in {
      decorated.name shouldBe "Alice"
      decorated.role shouldBe Roles.villager
      decorated.faction shouldBe Faction._Villager
      decorated.isAlive shouldBe true
    }

    "override vote to vote twice" in {
      decorated.vote(target) shouldBe "Alice votes TWICE for Bob!"
    }

    "die returns decorated dead player" in {
      decorated.die.isAlive shouldBe false
      decorated.die.isInstanceOf[DoubleVoteDecorator] shouldBe true
    }

    "revive returns decorated alive player" in {
      decorated.die.revive.isAlive shouldBe true
    }
  }
}