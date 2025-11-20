package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.werwolf.model.{Amor, Terrorist, Villager, Werwolf, Witch}

class RolesSpec extends AnyWordSpec {

"the functions from werwolf" should {
        "return String werwolf, false, vote" in {
            val werwolf = Werwolf("Hans")
            val player = Villager("Berta")
            val result_role = werwolf.role
            val result_die = werwolf.die
            val result_vote = werwolf.vote(player)
            result_role should be("Werwolf")
            result_die.isAlive should be(false)
            result_vote should be("Werwolf Hans votes for Berta to die")
        }
    }

    "the functions from Villager" should {
        "return String Villager, false, vote" in {
            val villager = Villager("Hansi")
            val player = Villager("Bertai")
            val result_role = villager.role
            val result_die = villager.die
            val result_vote = villager.vote(player)
            result_role should be("Villager")
            result_die.isAlive should be(false)
            result_vote should be("Villager Hansi votes for Bertai to die")
        }
    }

    "the functions from Amor" should {
        "return String Amor, false, vote" in {
            val amor = Amor("Herman")
            val player = Villager("Bert")
            val result_role = amor.role
            val result_die = amor.die
            val result_vote = amor.vote(player)
            result_role should be("Amor")
            result_die.isAlive should be(false)
            result_vote should be("Amor Herman votes for Bert to die")
        }
    }

    "the functions from Terrorist" should {
        "return String Terrorist, false, vote" in {
            val terrorist = Terrorist("Hannes")
            val player = Villager("Brta")
            val result_role = terrorist.role
            val result_die = terrorist.die
            val result_vote = terrorist.vote(player)
            result_role should be("Terrorist")
            result_die.isAlive should be(false)
            result_vote should be("Terrorist Hannes votes for Brta to die")
        }
    }

    "the functions from Witch" should {
        "return String Witch, false, vote" in {
            val witch = Witch("Han")
            val player = Villager("Bertas")
            val result_role = witch.role
            val result_die = witch.die
            val result_vote = witch.vote(player)
            result_role should be("Witch")
            result_die.isAlive should be(false)
            result_vote should be("Witch Han votes for Bertas to die")
        }
    }
}