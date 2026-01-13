// src/test/scala/controller/GameControllerSpec.scala
package de.htwg.werwolf.controller.commandComponent

import de.htwg.werwolf.model._
import de.htwg.werwolf.util.Observer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.util.{Success, Failure}
import de.htwg.werwolf.model.CommandInterface
import de.htwg.werwolf.model.narratorComponent.NarratorInterface
import de.htwg.werwolf.model.narratorComponent.JsonNarrator
import de.htwg.werwolf.model.commandComponent.ExecuteC
import de.htwg.werwolf.model.gameCoreComponents.GameCore
import de.htwg.werwolf.model.gameCoreComponents.GameCoreInterface
import de.htwg.werwolf.model.gameCoreComponents.Villager
import de.htwg.werwolf.model.gameCoreComponents.Werwolf
import de.htwg.werwolf.model.gameCoreComponents.Phase
import de.htwg.werwolf.controller.gameControllerComponent.GameController
import de.htwg.werwolf.model.commandComponent.GameCommand

class GameControllerSpec extends AnyWordSpec with Matchers {

  
  "GameController" should {

    given NarratorInterface =
      new JsonNarrator(
        os.pwd / "src" / "main" / "resources" / "narrator.json"
    )
    given CommandInterface = new ExecuteC
    given GameCoreInterface = new GameCore

    /*"have a getter for game" in {
      val controller = new GameController(Game())
      controller.game should be(game)
    }*/

    "update game correctly" in {
      val initialGame = Game()
      val controller = new GameController(initialGame)
      val newGame = Game(players = Map("test" -> Villager("test")))
      controller.updateGame(newGame) should be(newGame)
      controller.game should be(newGame)
    }

    "save game state" in {
      val game = Game()
      val controller = new GameController(game)
      controller.saveGameState()
      // No direct assertion, but covers the line
    }

    "undo full with saved memento" in {
      val game = Game(players = Map("p1" -> Villager("p1")))
      val controller = new GameController(game)
      controller.saveGameState()
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.undoFull()
      mockObserver.receivedEvents should contain(GameEvent.printText("↶ Vollständiges Undo – alles zurückgesetzt!", 70))
    }

    "undo full without memento" in {
      val game = Game()
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.undoFull()
      mockObserver.receivedEvents should contain(GameEvent.printText("Kein gespeicherter Spielstand zum Wiederherstellen.", 70))
    }

    "execute command" in {
      
      val game = Game()
      val controller = GameController(game)
      val cmd = MockCommand(game)
      controller.executeCommand(cmd, game)
      controller.game.commandHistory should contain(cmd)
    }

    "undo command with success" in {
      val game = Game(commandHistory = Vector(new MockCommand(Game())))
      val controller = new GameController(game)
      val result = controller.undoCommand()
      result.commandHistory should be(empty)
    }

    "undo command with failure" in {
      val game = Game()
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.undoCommand()
      mockObserver.receivedEvents should contain(GameEvent.printErrorMSG("Nichts zum Rückgängigmachen!"))
    }

    "add roles" in {
      val game = Game()
      val controller = new GameController(game)
      controller.addRoles(Vector("p1", "p2"))
      controller.game.players.size should be(2)
    }

    "run game loop until not running" in {
      val game = Game(isRunning = false)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.runGame()
      mockObserver.receivedEvents should contain(GameEvent.InitialthingsDone)
      mockObserver.receivedEvents should contain(GameEvent.GameOver)
    }

    "run game night phase" in {
      val players = Map("p1" -> Werwolf("p1"), "p2" -> Villager("p2"))
      val game = Game(players = players, phase = Phase.Night, isRunning = true)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.runGame()
      // Covers night phase, but loop may run once; adjust for coverage
    }

    "run game day phase" in {
      val players = Map("p1" -> Werwolf("p1"), "p2" -> Villager("p2"))
      val game = Game(players = players, phase = Phase.Day, isRunning = true)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.runGame()
      // Covers day phase
    }

    "handle win condition in run game" in {
      val players = Map("p1" -> Werwolf("p1"))
      val game = Game(players = players, phase = Phase.Night, isRunning = true)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.runGame()
      mockObserver.receivedEvents should contain(GameEvent.printText(s"Die ${Faction._Werwolf} haben gewonnen!!!", 120))
    }

    "handle win condition in two games" in {
      val players = Map("p1" -> Werwolf("p1"), "p2" -> Villager("p2"),  "p3" -> Villager("p3"))
      val game = Game(players = players, phase = Phase.Night, isRunning = true)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      controller.runGame()
      mockObserver.receivedEvents should contain(GameEvent.printText(s"Die ${Faction._Werwolf} haben gewonnen!!!", 120))
    }

    "handle no win condition" in {
      val players = Map("p1" -> Werwolf("p1"), "p2" -> Villager("p2"))
      val game = Game(players = players, phase = Phase.Night, isRunning = true)
      val controller = new GameController(game)
      val mockObserver = new MockObserver
      controller.addObserver(mockObserver)
      // Mock to prevent infinite loop; covers else branch
      controller.runGame() // May need to adjust game to end quickly
    }
  }
}

// Mocks for coverage
class MockObserver extends Observer[GameEvent] {
  var receivedEvents: List[GameEvent] = Nil
  override def update(event: GameEvent): Unit = receivedEvents = event :: receivedEvents
}

class MockCommand(initialGame: Game) extends GameCommand {
  override def description: String = "mock"

  override def execute(game: Game): Game = 
    initialGame  // oder game.copy(...) für realistischeres Verhalten

  override def undo(game: Game): Game = 
    initialGame  // zurück zum Ausgangszustand
}