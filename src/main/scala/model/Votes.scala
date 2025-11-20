// src/main/scala/model/Votes.scala
package de.htwg.werwolf.model

case class Votes(votes: Map[String, Int] = Map.empty):

  def addVote(player: String): Votes =
    val current = votes.getOrElse(player, 0)
    copy(votes = votes.updated(player, current + 1))

  def getVotedPlayer: Option[String] =
    if votes.isEmpty then None
    else Some(votes.maxBy(_._2)._1)
