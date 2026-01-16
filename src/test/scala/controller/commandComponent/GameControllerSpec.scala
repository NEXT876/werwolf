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
  }
}
