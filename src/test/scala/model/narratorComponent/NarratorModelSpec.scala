// src/test/scala/de/htwg/werwolf/model/narratorComponent/NarratorModelSpec.scala
package de.htwg.werwolf.model.narratorComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import upickle.default.*

class NarratorModelSpec extends AnyWordSpec with Matchers:

  "NarratorModel JSON codecs" should {

    "round-trip Root/Night/Day via upickle" in {
      val night = Night(
        Start = List("n1"),
        Werwolf = List("w1", "w2"),
        Amor = List("a1"),
        Witch = List("wi1")
      )
      val day = Day(Start = List("d1", "d2"))
      val root = Root(night, day)

      val json = write(root)
      val decoded = read[Root](json)

      decoded shouldBe root
    }
  }

