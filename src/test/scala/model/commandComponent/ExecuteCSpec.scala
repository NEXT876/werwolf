package model.commandComponent
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.commandComponent.{ExecuteC,GameCommand}
import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.model.{Game,CommandInterface}

class ExecuteCSpec extends AnyWordSpec with Matchers {

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
  }
}
