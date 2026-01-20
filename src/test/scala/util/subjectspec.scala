package de.htwg.werwolf.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class SubjectSpec extends AnyWordSpec with Matchers {

  // Konkrete Implementierung zum Testen
  class TestSubject extends Subject[String] {
    def fire(event: String): Unit =
      notifyObservers(event)
  }

  class TestObserver extends Observer[String] {
    var last: Option[String] = None
    override def update(state: String): Unit =
      last = Some(state)
  }

  "Subject" should {

    "addObserver and notifyObservers" in {
      val subject = new TestSubject
      val obs = new TestObserver

      subject.addObserver(obs)
      subject.fire("event1")

      obs.last shouldBe Some("event1")
    }

    "not add duplicate observers" in {
      val subject = new TestSubject
      val obs = new TestObserver

      subject.addObserver(obs)
      subject.addObserver(obs) // bewusst doppelt
      subject.fire("event2")

      obs.last shouldBe Some("event2") // nur einmal, aber ausgeführt
    }

    "removeObserver" in {
      val subject = new TestSubject
      val obs = new TestObserver

      subject.addObserver(obs)
      subject.removeObserver(obs)
      subject.fire("event3")

      obs.last shouldBe None
    }

    "handle observer removing itself during notify" in {
      val subject = new TestSubject

      val selfRemovingObserver = new Observer[String] {
        override def update(state: String): Unit =
          subject.removeObserver(this)
      }

      val normalObserver = new TestObserver

      subject.addObserver(selfRemovingObserver)
      subject.addObserver(normalObserver)

      subject.fire("event4")
      subject.fire("event5")

      // normalObserver muss weiterhin Updates bekommen
      normalObserver.last shouldBe Some("event5")
    }
  }
}
