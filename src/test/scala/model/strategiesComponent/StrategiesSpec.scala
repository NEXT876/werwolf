import de.htwg.werwolf.strategies.{AmorAction, NightActionStrategy, NoAction, TerroristAction, VillagerAction, WerwolfAction, WitchAction}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model._
import de.htwg.werwolf.model.Roles

class NightActionStrategySpec extends AnyFlatSpec with Matchers {

  // DummyPlayer für Tests
  case class DummyPlayer(name: String, var isAlive: Boolean, role: Roles) extends Player {
    def die = copy(isAlive = false)
    def revive = copy(isAlive = true)
    def faction: Faction = ???

    def nightAction: NightActionStrategy = NoAction
    def vote(target: Player): String = s"$name votes for ${target.name}"
    def winCondition(players: Map[String, Player]): Boolean = ???
  }

  val game = Game()

  "NightActionStrategy.canAct" should "return true if player is alive" in {
    val player = DummyPlayer("AlivePlayer", true, Roles.villager)
    WerwolfAction.canAct(player) shouldBe true
    WitchAction.canAct(player) shouldBe true
    NoAction.canAct(player) shouldBe true
  }

  it should "return false if player is dead" in {
    val player = DummyPlayer("DeadPlayer", false, Roles.villager)
    WerwolfAction.canAct(player) shouldBe false
    WitchAction.canAct(player) shouldBe false
    NoAction.canAct(player) shouldBe false
  }

  "NightActionStrategy.performAction" should "not throw exceptions" in {
    val alivePlayer = DummyPlayer("Alice", true, Roles.villager)
    val deadPlayer = DummyPlayer("Bob", false, Roles.villager)

    noException should be thrownBy WerwolfAction.performAction(alivePlayer, game)
    noException should be thrownBy WitchAction.performAction(alivePlayer, game)
    noException should be thrownBy TerroristAction.performAction(alivePlayer, game)
    noException should be thrownBy AmorAction.performAction(alivePlayer, game)
    noException should be thrownBy VillagerAction.performAction(alivePlayer, game)
    noException should be thrownBy NoAction.performAction(alivePlayer, game)

    // Auch bei toten Spielern testen
    noException should be thrownBy WerwolfAction.performAction(deadPlayer, game)
    noException should be thrownBy WitchAction.performAction(deadPlayer, game)
    noException should be thrownBy NoAction.performAction(deadPlayer, game)
  }

}
