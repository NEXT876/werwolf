// src/main/scala/de/htwg/werwolf/model/Subject.scala
package de.htwg.werwolf.util

trait Observer[-S]:
  def update(state: S): Unit

trait Subject[S]:
  private var observers: Vector[Observer[S]] = Vector.empty

  def addObserver(obs: Observer[S]): Unit =
    if !observers.contains(obs) then
      observers = observers :+ obs   // Duplikate vermeiden + performant append

  def removeObserver(obs: Observer[S]): Unit =
    observers = observers.filterNot(_ == obs)

  protected def notifyObservers(Event : S): Unit =
    // Kopie + reverse = safe gegen ConcurrentModification, wenn Observer sich selbst entfernt
    observers.reverse.foreach(_.update(Event))
    