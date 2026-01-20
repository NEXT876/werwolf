package model.commandComponent
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.commandComponent.{ExecuteC,GameCommand}
import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.model.{Game,CommandInterface}
import de.htwg.werwolf.model.Phase
import de.htwg.werwolf.model.gameCoreComponents.Votes
import de.htwg.werwolf.fileIO.IOInterface
import de.htwg.werwolf.model.GameCoreInterface
import de.htwg.werwolf.model.NarratorInterface

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.ArgumentMatchers

class ExecuteCSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ExecuteC" should {

    "execute a command and append it to commandHistory" in {
      val executor = ExecuteC()
      val dummyGame = Game()
      val cmd = new GameCommand {
        def execute(game: Game): Game = game
        def undo(game: Game): Game = game
      }

      val updatedGame = executor.executeCommand(cmd, dummyGame)
      updatedGame.commandHistory should contain(cmd)
    }

    "undoLast removes last command after undo" in {
      val executor = ExecuteC()
      val cmd = new GameCommand {
        def execute(game: Game): Game = game
        def undo(game: Game): Game = game
      }

      val gameWithCmd = Game(commandHistory = Vector(cmd))
      val result = executor.undoLast(gameWithCmd)
      result.isSuccess shouldBe true
      result.get.commandHistory shouldBe empty
    }

    "save and undo via mementos works" in {
      val executor = ExecuteC()
      val dummyGame = Game()

      given CommandInterface = executor

      executor.save(dummyGame)
      noException should be thrownBy executor.undo(dummyGame)
    }

    "undoLast returns Failure(NothingToUndo) when commandHistory is empty" in {
      given NarratorInterface = mock[NarratorInterface]
      given CommandInterface  = new ExecuteC()   // echte Implementierung
      given GameCoreInterface = mock[GameCoreInterface]
      given IOInterface       = mock[IOInterface]

      val g = Game(
        players = Map.empty,
        phase = Phase.Day,
        day = 1,
        votes = Votes(),
        isRunning = true,
        commandHistory = Vector.empty // 👈 entscheidend
      )

      val result = summon[CommandInterface].undoLast(g)

      result.isFailure shouldBe true
    }
    "undo does nothing when no savepoints exist (else branch)" in {
      val ci = mock[CommandInterface]
      given CommandInterface = ci

      val exec = ExecuteC()   // echtes Objekt, saves ist leer

      val g = Game(
        players = Map.empty,
        phase = Phase.Day,
        day = 1,
        votes = Votes(),
        isRunning = true,
        commandHistory = Vector.empty
      )

      exec.undo(g)  // saves.nonEmpty == false → else-Pfad

      verify(ci, never()).restoreFromMemento(any(), any())
    }
  }
}
