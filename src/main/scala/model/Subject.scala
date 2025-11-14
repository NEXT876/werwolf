// src/main/scala/de/htwg/werwolf/model/Subject.scala
package de.htwg.werwolf.model

trait Subject {
  private var observers: List[Observer] = Nil
  
  def addObserver(o: Observer): Unit = 
    observers = o :: observers
  
  def removeObserver(o: Observer): Unit = 
    observers = observers.filter(_ != o)
    
  protected def notifyObservers(event: GameEvent): Unit = 
    observers.foreach(_.update(event))
}