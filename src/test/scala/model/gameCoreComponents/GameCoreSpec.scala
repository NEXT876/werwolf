package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.werwolf.model.gameCoreComponents.Votes
import de.htwg.werwolf.model.Game

class VotesSpec extends AnyWordSpec {
  "the function addVote" should {
    "return the new votes Map" in {
      val votes0 = Votes()
      val game = Game()

      val votes1 = votes0.addVote("Bob", game)
      votes1.votes should be(Map("Bob" -> 1))

      val votes2 = votes1.addVote("Bob", game)
      votes2.votes should be(Map("Bob" -> 2))

      val votes3 = votes2.addVote("Bob", game)
      votes3.votes should be(Map("Bob" -> 3))

      val votes4 = votes3.addVote("Paula", game)
      votes4.votes should be(Map("Bob" -> 3, "Paula" -> 1))

      val votes5 = votes4.addVote("Paula", game)
      votes5.votes should be(Map("Bob" -> 3, "Paula" -> 2))

      votes5.getVotedPlayer(game) should be(Some("Bob"))
    }
    "return nothing for no votes" in {
      val votes = Votes()

      votes.getVotedPlayer(Game()) should be(None)
    }
  }
}
