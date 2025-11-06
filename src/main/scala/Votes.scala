package de.htwg.werwolf

object Votes {
  private var votes: Map[String, Int] = Map.empty

  def addVote(player: String): Unit = {
    val current = votes.getOrElse(player, 0)
    votes = votes.updated(player, current + 1)
  }

  def getVotedPlayer: String =
    votes.maxBy(_._2)._1
}
