package de.htwg.werwolf

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class VotesSpec extends AnyWordSpec {
  "the function addVote" should {
    "return the new votes Map" in {
      val VotesObject = Votes()

      VotesObject.addVote("Bob") should be (Map("Bob" -> 1))
      VotesObject.addVote("Bob") should be (Map("Bob" -> 2))
      VotesObject.addVote("Bob") should be (Map("Bob" -> 3))
      VotesObject.addVote("Paula") should be (Map("Bob" -> 3, "Paula" -> 1))
      VotesObject.addVote("Paula") should be (Map("Bob" -> 3, "Paula" -> 2))

      VotesObject.getVotedPlayer should be ("Bob")
    }
  }
}
