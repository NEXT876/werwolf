package de.htwg.werwolf.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import de.htwg.werwolf.model.strategiesComponent.*
import de.htwg.werwolf.model.gameCoreComponents.*
import de.htwg.werwolf.model.commandComponent.*
import de.htwg.werwolf.model.{Faction, Phase}

class StrategiesFullCoverageSpec extends AnyWordSpec with Matchers {

  given CommandInterface = ExecuteC()

  def game(players: Map[String, Player], day: Int = 0) =
    Game(
      players = players,
      phase = Phase.Night,
      day = day,
      votes = Votes(),
      pendingNightActors = players.keySet,
      isRunning = true,
      commandHistory = Vector()
    )

  "WerwolfAction" should {
    "call all methods" in {
      val w = Werwolf("W")
      val v = Villager("V")
      val g = game(Map("W" -> w, "V" -> v))

      WerwolfAction.canAct(w, g)
      WerwolfAction.possibleTargets(w, g)
      WerwolfAction.execute(w, "V", g)
    }
  }

  "WitchAction" should {
    "call all methods" in {
      val witch = Witch("Hexe")
      val vAlive = Villager("V1")
      val vDead = Villager("V2", isAlive = false)
      val g = game(Map("Hexe" -> witch, "V1" -> vAlive, "V2" -> vDead))

      WitchAction.canAct(witch, g)
      WitchAction.possibleTargets(witch, g)
      WitchAction.execute(witch, "V1", g)
      WitchAction.execute(witch, "V2", g)
    }
  }

  "voteAction" should {
    "call all methods" in {
      val v1 = Villager("A")
      val v2 = Villager("B")
      val g = game(Map("A" -> v1, "B" -> v2))

      voteAction.canAct(v1, g)
      voteAction.possibleTargets(v1, g)
      voteAction.execute(v1, "B", g)
    }
  }

  "AmorAction" should {
    "call all methods" in {
      val a = Amor("A")
      val v = Villager("V")
      val g = game(Map("A" -> a, "V" -> v), day = 0)

      AmorAction.canAct(a, g)
      AmorAction.possibleTargets(a, g)
      assertThrows[NotImplementedError] {
        AmorAction.execute(a, "V", g)
      }
    }
  }

  "VillagerAction" should {
    "call all methods" in {
      val v = Villager("V")
      val g = game(Map("V" -> v))

      VillagerAction.canAct(v, g)
      assertThrows[NotImplementedError] {
        VillagerAction.possibleTargets(v, g)
      }      
        VillagerAction.execute(v, "V", g)
    }
  }

  "TerroristAction" should {
    "call all methods" in {
      val t = Terrorist("T")
      val g = game(Map("T" -> t))

      TerroristAction.canAct(t, g)
      assertThrows[NotImplementedError] {
        TerroristAction.possibleTargets(t, g)
      }
      assertThrows[NotImplementedError] {
        TerroristAction.execute(t, "T", g)
      }
    }
  }

  "NoAction" should {
    "call all methods" in {
      val v = Villager("V")
      val g = game(Map("V" -> v))

      NoAction.canAct(v, g)
      assertThrows[NotImplementedError] {
        NoAction.possibleTargets(v, g)
      }
      assertThrows[NotImplementedError] {
        NoAction.execute(v, "V", g)
      }
    }
  }
}
