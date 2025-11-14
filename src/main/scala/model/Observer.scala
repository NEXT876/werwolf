package de.htwg.werwolf.model

trait Observer {
  def update(event: GameEvent): Unit
}