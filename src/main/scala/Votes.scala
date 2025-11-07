package de.htwg.werwolf

class Votes(initialVotes: Map[String, Int] = Map.empty):

  def addVote(player: String): Unit =
    val current = votes.getOrElse(player, 0)
    votes = votes.updated(player, current + 1)

  def getVotedPlayer: String =
    votes.maxBy(_._2)._1
