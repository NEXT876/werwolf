package de.htwg.werwolf.controller.gameControllerComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.ArgumentMatchers

import de.htwg.werwolf.model._
import de.htwg.werwolf.fileIO.IOInterface
import de.htwg.werwolf.model.commandComponent._

import scala.util.{Success, Failure}
import de.htwg.werwolf.model.gameCoreComponents.Villager
import de.htwg.werwolf.model.gameCoreComponents.Werwolf
import de.htwg.werwolf.model.gameCoreComponents.Votes

class GameControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameController" should {

    "saveIntoFile calls io.write with memento" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(
        players        = Map.empty,
        phase          = Phase.Day,
        day            = 1,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]
      )

      val memento = GameMemento(
        players        = Map.empty,
        phase          = Phase.Day,
        day            = 1,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]
      )

      when(ci.createMemento(initialGame)).thenReturn(memento)
      when(io.extension).thenReturn(".json")

      val controller = new GameController(initialGame)
      controller.saveIntoFile("foo")

      verify(io).write(any(), ArgumentMatchers.eq(memento))
    }

    "loadFromFile restores game using command interface" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(
        players        = Map.empty,
        phase          = Phase.Day,
        day            = 1,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]

      )

      val memento = GameMemento(
        players        = Map.empty,
        phase          = Phase.Night,
        day            = 5,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]

      )

      val restoredGame = initialGame.copy(phase = Phase.Night, day = 5)

      when(io.read(any())).thenReturn(memento)
      when(ci.restoreFromMemento(memento, initialGame)).thenReturn(restoredGame)

      val controller = new GameController(initialGame)
      controller.loadFromFile("testSave")

      controller.game.day   shouldBe 5
      controller.game.phase shouldBe Phase.Night

      verify(io).read(any())
      verify(ci).restoreFromMemento(memento, initialGame)
    }

    "executeCommand updates game" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(
        players        = Map.empty,
        phase          = Phase.Day,
        day            = 1,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]

      )

      val cmd         = mock[GameCommand]
      val updatedGame = initialGame.copy(day = 2)

      when(ci.executeCommand(cmd, initialGame)).thenReturn(updatedGame)

      val controller = new GameController(initialGame)
      controller.executeCommand(cmd, initialGame).day shouldBe 2
      controller.game.day shouldBe 2
    }

    "undoCommand success and failure" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(
        players        = Map.empty,
        phase          = Phase.Day,
        day            = 1,
        votes          = null,
        isRunning      = true,
        commandHistory = Vector.empty[GameCommand]

      )

      val undoneGame = initialGame.copy(day = 99)
      when(ci.undoLast(initialGame)).thenReturn(Success(undoneGame))

      val ctrl1 = new GameController(initialGame)
      val r1   = ctrl1.undoCommand()
      r1.day shouldBe 99
      ctrl1.game.day shouldBe 99

      when(ci.undoLast(any())).thenReturn(Failure(new Exception("none")))
      val ctrl2 = new GameController(initialGame)
      val r2   = ctrl2.undoCommand()
      r2.day shouldBe 1
    }

    "countAlivePlayer returns 0,0 with empty map" in {
        given NarratorInterface = mock[NarratorInterface]
        given CommandInterface  = mock[CommandInterface]
        given GameCoreInterface = mock[GameCoreInterface]
        given IOInterface       = mock[IOInterface]

        val controller = new GameController(
          Game(Map.empty, Phase.Day, 1, null, true, Vector.empty[GameCommand])
        )
        controller.countAlivePlayer() shouldBe (0, 0)
      }

    "addRoles updates game via GameCore" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(Map.empty, Phase.Day, 1, null, true, Vector.empty[GameCommand]
)
      val gAfterRoles = initialGame.copy(day = 7)

      when(gc.addRoles(Vector("a","b"), initialGame)).thenReturn(gAfterRoles)

      val controller = new GameController(initialGame)
      controller.addRoles(Vector("a","b"))
      controller.game.day shouldBe 7
    }

    "switchPhase toggles and finishNightPhase runs" in {


        given NarratorInterface = mock[NarratorInterface]
        given CommandInterface  = mock[CommandInterface]
        given GameCoreInterface = mock[GameCoreInterface]
        given IOInterface       = mock[IOInterface]

        val controller = new GameController(
          Game(Map.empty, Phase.Night, 1, null, true, Vector.empty[GameCommand])
        )

        controller.switchPhase().phase shouldBe Phase.Day
        // finishNightPhase should not throw
        controller.finishNightPhase()
      }

    "runCurrentPhase runs for both day + night" in {
        given NarratorInterface = mock[NarratorInterface]
        given CommandInterface  = mock[CommandInterface]
        given GameCoreInterface = mock[GameCoreInterface]
        given IOInterface       = mock[IOInterface]

        val controllerDay = new GameController(
          Game(Map.empty, Phase.Day, 1, null, true, Vector.empty[GameCommand])
        )
        controllerDay.runCurrentPhase()

        val controllerNight = new GameController(
          Game(Map.empty, Phase.Night, 1, null, true, Vector.empty[GameCommand])
        )
        controllerNight.runCurrentPhase()
      }

    "checkIfGameEnd executes GameEndCommand" in {
      val narrator = mock[NarratorInterface]
      val ci       = mock[CommandInterface]
      val gc       = mock[GameCoreInterface]
      val io       = mock[IOInterface]

      given NarratorInterface = narrator
      given CommandInterface  = ci
      given GameCoreInterface = gc
      given IOInterface       = io

      val initialGame = Game(Map.empty, Phase.Day, 1, null, true, Vector.empty[GameCommand]
)
      when(ci.executeCommand(any[GameCommand](), any[Game]())).thenReturn(initialGame.copy(isRunning = false))

      val controller = new GameController(initialGame)
      controller.checkIfGameEnd(Some(Faction._Werwolf))
      verify(ci).executeCommand(any[GameCommand](), any[Game]())
    }
    "saveGameState executes createMemento" in {
  given NarratorInterface = mock[NarratorInterface]
  given CommandInterface  = mock[CommandInterface]
  given GameCoreInterface = mock[GameCoreInterface]
  given IOInterface       = mock[IOInterface]

  val g = Game(Map.empty, Phase.Day, 1, null, true, Vector())
  val ctrl = new GameController(g)

  ctrl.saveGameState()
  verify(summon[CommandInterface]).createMemento(g)
}
    "undoFull restores game when memento exists" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val g = Game(Map.empty, Phase.Day, 1, null, true, Vector())
      val m = GameMemento(Map.empty, Phase.Night, 2, null, true)

      when(summon[CommandInterface].restoreFromMemento(m, g))
        .thenReturn(g.copy(day = 2))

      val ctrl = new GameController(g)

      // Hack für Coverage: Reflection oder sichtbarer Setter
      val field = classOf[GameController].getDeclaredField("savedMemento")
      field.setAccessible(true)
      field.set(ctrl, Some(m))

      ctrl.undoFull()
      ctrl.game.day shouldBe 2
    }
    "submitNightChoice skip branch executes" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val p = Werwolf("W")
      val g = Game(
        Map("W" -> p),
        Phase.Night,
        1,
        Votes(),
        isRunning = true,
        commandHistory = Vector(),
        pendingNightActors = Set()
      )

      val ctrl = new GameController(g)
      ctrl.submitNightChoice("W", "W") // skip
      ctrl.game.pendingNightActors shouldBe Set("W")
    }
    "submitvoting kills when no werewolves remain" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val wolf = Werwolf("W")
      val vill = Villager("V")

      val g = Game(
        Map("W" -> wolf, "V" -> vill),
        Phase.Night,
        1,
        Votes(Map("V" -> 1)),
        isRunning = true,
        commandHistory = Vector(),
        pendingNightActors = Set()
      )

      when(summon[CommandInterface].executeCommand(any(), any()))
        .thenReturn(g.copy(players = g.players.updated("V", vill.die)))

      val ctrl = new GameController(g)
      ctrl.submitvoting("W", "V")

      ctrl.game.players("V").isAlive shouldBe false
    }
    "runNightPhase enters player loop" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val wolf = Werwolf("W")

      val g = Game(
        players = Map("W" -> wolf),
        phase = Phase.Night,
        day = 1,
        votes = Votes(),
        isRunning = true,
        commandHistory = Vector(),
        pendingNightActors = Set()
      )

      val ctrl = new GameController(g)
      ctrl.runNightPhase() // muss Loop betreten
    }
    "undoFull with no saved memento triggers None branch" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val g = Game()
      val ctrl = new GameController(g)

      ctrl.undoFull() // savedMemento = None
    }
    "submitvoting executes dayAction branch" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      when(summon[CommandInterface].executeCommand(any(), any()))
        .thenAnswer(inv => inv.getArgument(1, classOf[Game]))


      val v1 = Villager("A")
      val v2 = Villager("B")

      val g = Game(
        players = Map("A" -> v1, "B" -> v2),
        phase = Phase.Day,
        pendingNightActors = Set("A")
      )

      val ctrl = new GameController(g)
      ctrl.submitvoting("A", "B")
    }
    "submitvoting no voted player hits None branch" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      when(summon[CommandInterface].executeCommand(any(), any()))
        .thenAnswer(inv => inv.getArgument(1, classOf[Game]))

      val w = Werwolf("W")

      val g = Game(
        players = Map("W" -> w),
        phase = Phase.Night,
        votes = Votes(), // leer ⇒ None
        pendingNightActors = Set("W")
      )

      val ctrl = new GameController(g)
      ctrl.submitvoting("W", "W")
    }

    "switchPhase from Day to Night covers else branch" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val ctrl = new GameController(Game(phase = Phase.Day))
      ctrl.switchPhase().phase shouldBe Phase.Night
    }
    "runGame executes initialThingsDone" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val ctrl = new GameController(Game())
      ctrl.runGame()
    }
    "checkIfGameEnd with None does nothing" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val ctrl = new GameController(Game())
      ctrl.checkIfGameEnd(None)
    }
    "submitNightChoice executes dayAction branch when phase is Day" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val v = Villager("V")

      val g = Game(
        players = Map("V" -> v),
        phase = Phase.Day,                 // 👈 entscheidend
        pendingNightActors = Set("V")
      )

      val ctrl = new GameController(g)
      ctrl.submitNightChoice("V", "X")      // playerName != target → Action-Zweig
    }
    "runNightPhase covers sortBy role comparison" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val wolf1 = Werwolf("W1")
      val wolf2 = Werwolf("W2")

      val g = Game(
        players = Map(
          "W1" -> wolf1,
          "W2" -> wolf2
        ),
        phase = Phase.Night,
        pendingNightActors = Set()
      )

      val ctrl = new GameController(g)
      ctrl.runNightPhase()

      ctrl.game.pendingNightActors should contain allOf ("W1", "W2")
    }
    "countAlivePlayer covers both werwolf and non-werwolf counts" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = mock[CommandInterface]
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val wolf = Werwolf("W", isAlive = true)
      val vill = Villager("V", isAlive = true)

      val g = Game(
        players = Map(
          "W" -> wolf,
          "V" -> vill
        ),
        phase = Phase.Day
      )

      val ctrl = new GameController(g)

      ctrl.countAlivePlayer() shouldBe (1, 1)
    }


  }
}
