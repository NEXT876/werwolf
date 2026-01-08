package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.werwolf.model.voting.Votes

class VotesSpec extends AnyWordSpec {
  "the function addVote" should {
    "return the new votes Map" in {
      val votes0 = Votes()

      val votes1 = votes0.addVote("Bob")
      votes1.votes should be(Map("Bob" -> 1))

      val votes2 = votes1.addVote("Bob")
      votes2.votes should be(Map("Bob" -> 2))

      val votes3 = votes2.addVote("Bob")
      votes3.votes should be(Map("Bob" -> 3))

      val votes4 = votes3.addVote("Paula")
      votes4.votes should be(Map("Bob" -> 3, "Paula" -> 1))

      val votes5 = votes4.addVote("Paula")
      votes5.votes should be(Map("Bob" -> 3, "Paula" -> 2))

      votes5.getVotedPlayer should be(Some("Bob"))
    }
    "return nothing for no votes" in {
      val votes = Votes()

      votes.getVotedPlayer should be(None)
    }
  }
}
