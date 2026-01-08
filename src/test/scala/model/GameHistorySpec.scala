import de.htwg.werwolf.model.voting.Votes
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.*

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

  // Minimal-Stub für Game
  class TestGame extends Game {
    var restored: Option[GameMemento] = None

    override def createMemento(): GameMemento = testMemento

    override def restoreFromMemento(m: GameMemento): Game = {
      restored = Some(m)
      this
    }
  }

  "GameHistory" should {

    "save pushes a memento onto the stack" in {
      val hist = new GameHistory
      val game = new TestGame

      hist.save(game)

      // Überprüfen: Der interne Stack soll 1 Element haben
      // Zugriff über Reflection vermeiden wir lieber,
      // also testen wir über undo, was das Memento wirklich nutzt.
      game.restored shouldBe None

      hist.undo(game)

      game.restored shouldBe Some(testMemento)
    }

    "undo pops the last memento and restores the game" in {
      val hist = new GameHistory
      val game = new TestGame

      hist.save(game)
      hist.save(game)

      hist.undo(game)

      game.restored shouldBe Some(testMemento)
    }

    "list should not throw an exception even when empty" in {
      val hist = new GameHistory
      noException shouldBe thrownBy(hist.list())
    }
  }
}
