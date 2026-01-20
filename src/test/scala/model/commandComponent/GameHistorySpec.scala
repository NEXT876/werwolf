
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.*
import de.htwg.werwolf.model.commandComponent.GameMemento
import de.htwg.werwolf.model.Phase
import de.htwg.werwolf.model.gameCoreComponents.Votes
import de.htwg.werwolf.model.commandComponent.ExecuteC
import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.model.commandComponent.GameCommand

class FakeCommandInterface extends CommandInterface {
  var lastRestored: Option[GameMemento] = None

  override def createMemento(game: Game): GameMemento =
    GameMemento(Map.empty, Phase.Day, 1, Votes(), isRunning = true)

  override def restoreFromMemento(m: GameMemento, game: Game): Game =
    lastRestored = Some(m)
    game

  override def executeCommand(cmd: GameCommand, game : Game): Game = ???
  override def undoLast(game : Game): Try[Game] = ???
  override def replay(game : Game): Unit = ???
}
class GameMementoSpec extends AnyWordSpec with Matchers:

  given FakeCommandInterface = new FakeCommandInterface
  val dummyGame = new Game {}

  "GameMemento" should {

    "save a memento when save is called" in {
      val memento = GameMemento(Map.empty, Phase.Day, 1, Votes(), isRunning = true)
      memento.save(dummyGame)
      noException should be thrownBy memento.undo(dummyGame)
    }

    "restore the last saved memento on undo" in {
      val ci = summon[FakeCommandInterface]
      val memento = GameMemento(Map.empty, Phase.Day, 1, Votes(), isRunning = true)

      memento.save(dummyGame)
      memento.undo(dummyGame)

      ci.lastRestored shouldBe defined
    }
  }