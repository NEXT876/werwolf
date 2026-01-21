package de.htwg.werwolf.model.narratorComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class JsonNarratorSpec extends AnyWordSpec with Matchers:

  "JsonNarrator" should {

    "return a non-empty night text for Werwolf" in {
      val narrator = new JsonNarrator()
      val res = narrator.randomNightNarratorTexte("Werwolf")
      res should not be empty
    }

    "return a non-empty night text for start" in {
      val narrator = new JsonNarrator()
      val res = narrator.randomNightNarratorTexte("Start")
      res should not be empty
    }

    "return a non-empty night text for Witch" in {
      val narrator = new JsonNarrator()
      val res = narrator.randomNightNarratorTexte("Witch")
      res should not be empty
    }

    "return a non-empty night text for Amor" in {
      val narrator = new JsonNarrator()
      val res = narrator.randomNightNarratorTexte("Amor")
      res should not be empty
    }

    "return a non-empty day text for Start" in {
      val narrator = new JsonNarrator()
      val res = narrator.randomDayNarratorTexte("Start")
      res should not be empty
    }

    "return empty string for unknown night role" in {
      val narrator = new JsonNarrator()
      narrator.randomNightNarratorTexte("Unknown") shouldBe ""
    }

    "return empty string for unknown day role" in {
      val narrator = new JsonNarrator()
      narrator.randomDayNarratorTexte("Foo") shouldBe ""
    }

    "not throw on valid roles (smoke test)" in {
      val narrator = new JsonNarrator()
      noException should be thrownBy narrator.randomNightNarratorTexte("Werwolf")
      noException should be thrownBy narrator.randomDayNarratorTexte("Start")
    }
  }
