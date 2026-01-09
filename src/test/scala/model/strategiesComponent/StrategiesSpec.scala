// src/test/scala/de/htwg/werwolf/model/strategiesComponent/NightActionStrategySpec.scala
package de.htwg.werwolf.model.strategiesComponent

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.werwolf.model._
import de.htwg.werwolf.model.gameCoreComponents.{Player, Roles, Phase}
import de.htwg.werwolf.model.commandComponent.{ExecuteC}
import de.htwg.werwolf.model.CommandInterface

class NightActionStrategySpec extends AnyFlatSpec {

  // Minimaler Dummy-Player – nur das Nötigste
  case class DummyPlayer(
    name: String,
    isAlive: Boolean = true,
    role: Roles = Roles.villager
  ) extends Player {
    def die: Player = copy(isAlive = false)
    def revive: Player = copy(isAlive = true)
    def faction: Faction = if role == Roles.werwolf then Faction._Werwolf else Faction._Villager
    def nightAction: NightActionStrategy = NoAction
    def vote(target: Player): String = s"$name votes for ${target.name}"
  }

  // Leeres Game reicht für "no exception"-Test
  private val emptyGame = Game()

  // given nur im Test-Scope
  given CommandInterface = ExecuteC()

  "NightActionStrategy.performAction" should "not throw exceptions for alive players" in {
    val alive = DummyPlayer("Alice", isAlive = true)

    noException should be thrownBy WerwolfAction.performAction(alive, emptyGame)
    noException should be thrownBy WitchAction.performAction(alive, emptyGame)
    noException should be thrownBy TerroristAction.performAction(alive, emptyGame)
    noException should be thrownBy AmorAction.performAction(alive, emptyGame)
    noException should be thrownBy VillagerAction.performAction(alive, emptyGame)
    noException should be thrownBy NoAction.performAction(alive, emptyGame)
  }

  it should "not throw exceptions for dead players" in {
    val dead = DummyPlayer("Bob", isAlive = false)

    noException should be thrownBy WerwolfAction.performAction(dead, emptyGame)
    noException should be thrownBy WitchAction.performAction(dead, emptyGame)
    noException should be thrownBy TerroristAction.performAction(dead, emptyGame)
    noException should be thrownBy AmorAction.performAction(dead, emptyGame)
    noException should be thrownBy VillagerAction.performAction(dead, emptyGame)
    noException should be thrownBy NoAction.performAction(dead, emptyGame)
  }
}