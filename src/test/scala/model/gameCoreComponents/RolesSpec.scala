package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.gameCoreComponents._
import de.htwg.werwolf.model.strategiesComponent.{VillagerAction,WerwolfAction,AmorAction,WitchAction,TerroristAction,VoteAction}
import de.htwg.werwolf.model.{Faction,Roles,NightActionStrategy,Game,CommandInterface}
import de.htwg.werwolf.fileIO.fileIOImpl.PlayerJson.terroristFormat

object DummyNightAction extends NightActionStrategy:

  def canAct(player: Player, game: Game): Boolean = true

  def possibleTargets(player: Player, game: Game): Vector[String] =
    game.players.keys.toVector.filterNot(_ == player.name)

  def execute(player: Player, target: String, game: Game)
             (using ci: CommandInterface): Game =
    game
final case class PassthroughDecorator(override val inner: Player) extends PlayerDecorator(inner):
  protected def copyWith(newPlayer: Player): Player = PassthroughDecorator(newPlayer)


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
      werwolf.nightAction shouldBe WerwolfAction

      val expected = werwolf.die.toString()
      werwolf.die.toString shouldBe expected
      werwolf.dayAction shouldBe VoteAction
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
      villager.nightAction shouldBe VillagerAction
      villager.dayAction shouldBe VoteAction

      val expected = villager.toString()
      villager.toString shouldBe expected
    }
  }


  "Amor" should {
    "have correct role, faction, vote and toString" in {
      val amor = Amor("Herman")
      val target = Villager("Bert")

      amor.role shouldBe Roles.amor
      amor.faction shouldBe Faction._Villager
      amor.die.isAlive shouldBe false
      amor.die.revive.isAlive shouldBe true
      amor.vote(target) shouldBe "Amor Herman votes for Bert to die"
      amor.nightAction shouldBe AmorAction
      amor.dayAction shouldBe VoteAction

      val expected = amor.toString()
      amor.toString shouldBe expected
    }
  }

  "Terrorist" should {
    "have correct role, faction, vote and toString" in {
      val terrorist = Terrorist("Hannes")
      val target = Villager("Brta")

      terrorist.role shouldBe Roles.terrorist
      terrorist.faction shouldBe Faction._Villager
      terrorist.die.isAlive shouldBe false
      terrorist.die.revive.isAlive shouldBe true
      terrorist.vote(target) shouldBe
        "Terrorist Hannes votes for Brta to die"
      terrorist.nightAction shouldBe TerroristAction
      terrorist.dayAction shouldBe VoteAction

      val expected = terrorist.toString()
      terrorist.toString() shouldBe expected
    }
  }

  "Witch" should {
    "have correct role, faction, vote and toString" in {
      val witch = Witch("Han")
      val target = Villager("Bertas")

      witch.role shouldBe Roles.witch
      witch.faction shouldBe Faction._Villager
      witch.die.isAlive shouldBe false
      witch.die.revive.isAlive shouldBe true
      witch.vote(target) shouldBe "Witch Han votes for Bertas to die"
      witch.nightAction shouldBe WitchAction
      witch.dayAction shouldBe VoteAction

      val expected = witch.toString()
      witch.toString() shouldBe expected
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

    "return the wrapped player via decorated" in {
      val player = Villager("Alice")
      val decorator = DoubleVoteDecorator(player)
      decorator.decorated shouldBe player
    }

    "decorated" should {
      "return the inner player" in {
        val base = Villager("Alice")
        val deco = DoubleVoteDecorator(base)

        deco.decorated shouldBe base
      }
    }

    "vote" should {
      "delegate voting to inner player" in {
        val base = Villager("Bob")
        val deco = DoubleVoteDecorator(base)

        deco.vote(target) should not be base.vote(target)
      }
    }

    "actions" should {
      "delegate nightAction and dayAction" in {
        val base = Villager("Clara")
        val deco = DoubleVoteDecorator(base)

        deco.nightAction shouldBe base.nightAction
        deco.dayAction shouldBe base.dayAction
      }
    }

    "toString" should {
      "delegate toString to inner player" in {
        val base = Villager("Dora")
        val deco = DoubleVoteDecorator(base)

        deco.toString shouldBe base.toString
      }
    }
  }

  "override vote behavior" in {
    val base = Villager("Bob")
    val deco = DoubleVoteDecorator(base)

    deco.vote(base) shouldBe "Bob votes TWICE for Bob!"
  }

  "return the inner player via decorated (line 26)" in {
      val base = Villager("Alice")
      val deco = PassthroughDecorator(base)

      deco.decorated shouldBe base
    }

  "delegate vote to the inner player (line 30)" in {
    val base = Villager("Bob")
    val deco = PassthroughDecorator(base)

    deco.vote(base) shouldBe base.vote(base)
  }
}