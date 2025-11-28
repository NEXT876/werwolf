package de.htwg.werwolf.test
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.werwolf.model.{Witch, Player, Game, KillCommand, HealCommand}
import de.htwg.werwolf.model.*
import de.htwg.werwolf.view.*

class CommandSpec extends AnyFlatSpec with Matchers {
    // Eine NightAction, die nichts tut – nur für Tests
    class NoOpNightAction extends NightActionStrategy {
        override def performAction(player: Player, game: Game): Unit = ()
    }
    case class DummyPlayer(name: String, var isAlive: Boolean, role: String) extends Player {
        def die = copy(isAlive = false)
        def revive = copy(isAlive = true)

        def nightAction: NightActionStrategy = new NoOpNightAction

        def vote(target: Player): String = target.name
    }



    "KillCommand" should "correctly describe the action" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Dorfbewohner")
        val game = Game()
        val command = KillCommand(killer, target, game)

        command.description shouldBe "Werwolf tötet Opfer"
    }

   /* it should "execute and mark the target as dead" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Dorfbewohner")
        val game = Game()
        val command = KillCommand(killer, target, game)

        // execute noch nicht implementiert, Test wird initially failen
        game.executeCommand(command)
        target.isAlive shouldBe false
    }*/

    /*it should "undo the kill and revive the target" in {
        val killer = DummyPlayer("Werwolf", true, "Werwolf")
        val target = DummyPlayer("Opfer", true, "Dorfbewohner")
        val game = Game()
        val command = KillCommand(killer, target, game)

        command.execute()
        command.undo()
        target.isAlive shouldBe true
    }*/

    "HealCommand" should "correctly describe the action" in {
        val witch = Witch("Hexe", true)
        val target = DummyPlayer("Opfer", false, "Dorfbewohner")
        val command = HealCommand(witch, target)

        command.description shouldBe "Hexe heilt Opfer"
    }

    /*it should "execute and revive a dead player" in {
        val witch = Witch("Hexe", true)
        val target = DummyPlayer("Opfer", false, "Dorfbewohner")
        val command = HealCommand(witch, target)

        command.execute()
        target.isAlive shouldBe true
    }*/

    it should "undo the heal and set the player back to dead" in {
    val witch = Witch("Hexe", true)
    val target = DummyPlayer("Opfer", false, "Dorfbewohner")
    val command = HealCommand(witch, target)

    command.execute()
    command.undo()
    target.isAlive shouldBe false
  }
}