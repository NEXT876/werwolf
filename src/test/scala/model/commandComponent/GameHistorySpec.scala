
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.*
import de.htwg.werwolf.model.commandComponent.GameMemento
import de.htwg.werwolf.model.Phase
import de.htwg.werwolf.model.gameCoreComponents.Votes
//import de.htwg.werwolf.model.commandComponent.CommandInterface
import de.htwg.werwolf.model.commandComponent.ExecuteC

class GameHistorySpec extends AnyWordSpec with Matchers {

  // Dummy-Memento, wir testen nur die Stack-Mechanik
  val testMemento = GameMemento(
    players = Map.empty,
    phase = Phase.Day,            // oder Phase.Night, je nach Modell
    day = 1,
    votes = Votes(Map.empty),     // oder wie auch immer Votes gebaut wird
    isRunning = true,
    commandHistory = Vector.empty
  )
/*
  // Minimal-Stub für Game
  class TestGame extends Game {
    var restored: Option[GameMemento] = None

    override def createMemento(): GameMemento = testMemento

    override def restoreFromMemento(m: GameMemento): Game = {
      restored = Some(m)
      this
    }*/
    }/*
  "GameHistory" should {
    "save pushes a memento onto the stack" in {
      given CommandInterface = new ExecuteC()
      val hist = GameMemento()  // leeres Memento als Start (felder egal)
      val game = TestGame()  // mit restore-Logik

      hist.save(game)
      game.restored shouldBe None  // nichts restored vor undo

      hist.undo(game)
      game.restored shouldBe Some(testMemento)  // ci.restoreFromMemento setzt es
    }

    "undo pops the last memento and restores the game" in {
      given CommandInterface = new ExecuteC()
      val hist = GameMemento()
      val game = TestGame()

      hist.save(game)  // push 1
      hist.save(game)  // push 2 (stackt, nicht überschreibt)

      hist.undo(game)  // pop 2, restore 2
      game.restored shouldBe Some(testMemento)  // letzter Save
    }

    "list should not throw an exception even when empty" in {
      val hist = GameMemento()
      noException should be thrownBy hist.list()
    }*/
  