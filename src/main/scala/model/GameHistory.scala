package de.htwg.werwolf.model

class GameHistory {
  private val saves = scala.collection.mutable.Stack[GameMemento]()

  def save(game: Game): Unit = {
    saves.push(game.createMemento())
    println(s"Spielstand ${saves.size} gespeichert")
  }

  def undo(game: Game): Unit = if (saves.nonEmpty) {
    game.restoreFromMemento(saves.pop())
    println("Zurück zum letzten Savepoint!")
  }

  def list(): Unit = saves.zipWithIndex.reverse.foreach {
    case (m, i) =>// println(s"$i: ${m}")
  }
}