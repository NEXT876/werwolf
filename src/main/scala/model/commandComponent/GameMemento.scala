// src/main/scala/de/htwg/werwolf/model/commands/GameMemento
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.voteComponent.Votes
import de.htwg.werwolf.model.phaseComponent.Phase
import de.htwg.werwolf.model.playerComponent.Player

case class GameMemento(
  players: Map[String, Player],
  phase: Phase,
  day: Int,
  votes: Votes,
  isRunning: Boolean,
  commandHistory: Vector[GameCommand]
)

