// de.htwg.werwolf.model.voting.votesInterface.scala
package de.htwg.werwolf.model.voteComponent

trait votesInterface:
    def addVote(player: String): Votes
    def getVotedPlayer: Option[String]
