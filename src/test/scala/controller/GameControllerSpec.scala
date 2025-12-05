package de.htwg.werwolf.controller

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.werwolf.model._
import de.htwg.werwolf.view.GameView

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

// Pragmatic test — eher grob, aber zielt darauf ab, viele Pfade in GameController zu durchlaufen.
class GameControllerSpec extends AnyWordSpec with Matchers {

  // Fake view, sammelt Aufrufe zum späteren Assert
  class FakeView extends GameView {
    val tipMessages = ListBuffer.empty[(String, Int)]
    var logoShown = 0
    var cleared = 0
    var printedRoles: Option[Vector[AnyRef]] = None
    var gameOverShown = false
    var askedAmount: Option[Int] = None
    var providedNames: Vector[String] = Vector.empty

    override def getPlayerAmount(): Int = 
      3

    override def getPlayerNames(playerAmount: Int): Vector[String] = {
      askedAmount = Some(playerAmount)
      // return some deterministic names
      Vector("Alice", "Bob", "Charlie")
    }

    override def printPlayerRoles(playerRoles: Vector[AnyRef]): Unit =
      printedRoles = Some(playerRoles)

    override def tiping(text: String, waitTime_ms: Int = 30): Unit =
      tipMessages += ((text, waitTime_ms))

    override def showLogo(): Unit = logoShown += 1
    override def clearScreen(): Unit = cleared += 1
    override def showGameOver(): Unit = gameOverShown = true
  }

  // Minimal dummy GameCommand to drive execute/undo
  class DummyCmd(val description: String = "dummy") extends GameCommand {
    override def execute(g: Game): Game = g.copy(day = g.day + 1)
    override def undo(g: Game): Game = g.copy(day = Math.max(1, g.day - 1))
  }

  "GameController" should {

    "handle undoFull when no saved memento exists" in {
      val fakeView = new FakeView
      val g = Game() // default game
      val controller = new GameController(g, fakeView)

      // call undoFull with no saved memento yet
      controller.undoFull()

      // view should have been informed about missing save
      fakeView.tipMessages.nonEmpty shouldBe true
      fakeView.tipMessages.last._1 should include("Kein gespeicherter Spielstand")
    }

    "executeCommand and produce saved memento so undoFull works afterwards" in {
      val fakeView = new FakeView

      // Make a game that simply delegates to default behavior
      val g = Game()
      val controller = new GameController(g, fakeView)

      val cmd = new DummyCmd()
      // executeCommand should save a memento and update the game (day+1)
      val beforeDay = controller.game.day
      controller.executeCommand(cmd)
      controller.game.day shouldBe beforeDay + 1

      // now undoFull should be able to restore (we expect a message about full undo)
      controller.undoFull()
      // The tip message should contain the full undo message
      fakeView.tipMessages.exists(_._1.contains("Vollständiges Undo")) shouldBe true
    }

    "undoCommand should call game's undoLast and update controller.game" in {
      val fakeView = new FakeView

      // Create a custom game instance that uses the default commandHistory handling.
      val baseGame = Game()
      val controller = new GameController(baseGame, fakeView)

      val cmd = new DummyCmd()
      // execute then undo
      controller.executeCommand(cmd)
      val afterExecDay = controller.game.day
      controller.undoCommand()
      // after undoCommand controller.game.day should have been rolled back
      controller.game.day shouldBe afterExecDay - 1
    }

    "start should call view methods and run a single game loop (via checkWinCondition)" in {
      val fakeView = new FakeView

      // create a custom game that will cause one iteration and then stop by returning a winner
      val Game = new Game(isRunning = false)
      val controller = new GameController(Game, fakeView)

      controller.start()

      val Game2 = new Game(isRunning = true, players = Map("Beate" -> Roles.werwolf.toPlayer("Beate"))) {
        override def runNightPhase(): Unit = {}
        override def runDayPhase(): Unit ={}
        }

       val controller2 = new GameController(Game2, fakeView)
       controller2.runGame()

      val Game3 = new Game(isRunning = true, players = Map("Beate" -> Roles.werwolf.toPlayer("Beate")), phase = Phase.Day) {
        override def runNightPhase(): Unit = {}
        override def runDayPhase(): Unit ={}
        }

       val controller3 = new GameController(Game3, fakeView)
       controller3.runGame()
      
      
      // Views should have been invoked in the process
      fakeView.logoShown should be >= 1
      fakeView.cleared should be >= 2
      fakeView.gameOverShown shouldBe true

      // getPlayerNames must have been called with getPlayerAmount result (3)
      fakeView.askedAmount shouldBe Some(3)
    }

    "update should handle printGameState and printnarratorText events" in {
      val fakeView = new FakeView
      val g = Game()
      val controller = new GameController(g, fakeView)

      // prepare sample players map: use Roles helper
      val p1 = Roles.werwolf.toPlayer("W1")
      val p2 = Roles.villager.toPlayer("V1")
      val players: Map[String, Player] = Map("W1" -> p1, "V1" -> p2)

      // call printGameState event
      controller.update(GameEvent.printGameState(players))
      // view should have printed roles (converted)
      fakeView.printedRoles.isDefined shouldBe true
      val printed = fakeView.printedRoles.get
      printed.exists(_.isInstanceOf[Tuple3[?, ?, ?]]) shouldBe true // we expect tuples (name, roleString, isAlive) but be permissive

      // call printnarratorText event
      controller.update(GameEvent.printnarratorText("Ein Text"))
      // tiping should have been called with the narrator text (last tip)
      fakeView.tipMessages.last._1 should include("Ein Text")
    }
  }
}
