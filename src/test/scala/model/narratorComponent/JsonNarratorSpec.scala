// src/test/scala/de/htwg/werwolf/model/narratorComponent/JsonNarratorSpec.scala
package de.htwg.werwolf.model.narratorComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.nio.file.Files
import upickle.default.*

class JsonNarratorSpec extends AnyWordSpec with Matchers:

  private def withTempJson(root: Root)(test: os.Path => Unit): Unit =
    val dir = Files.createTempDirectory("json-narrator-test")
    val file = dir.resolve("narrator.json")
    Files.writeString(file, write(root))
    test(os.Path(file))

  "JsonNarrator" should {

    "return a night text from the correct role list" in {
      val root = Root(
        Night(
          Start = List("ns1"),
          Werwolf = List("nw1", "nw2"),
          Amor = List("na1"),
          Witch = List("nwi1")
        ),
        Day(Start = List("ds1"))
      )

      withTempJson(root) { path =>
        val narrator = new JsonNarrator(path)
        val res = narrator.randomNightNarratorTexte("Werwolf")
        root.Night.Werwolf should contain (res)
      }
    }

    "return a day text from Start list" in {
      val root = Root(
        Night(
          Start = List("ns1"),
          Werwolf = List("nw1"),
          Amor = List("na1"),
          Witch = List("nwi1")
        ),
        Day(Start = List("ds1", "ds2"))
      )

      withTempJson(root) { path =>
        val narrator = new JsonNarrator(path)
        val res = narrator.randomDayNarratorTexte("Start")
        root.Day.Start should contain (res)
      }
    }

    "return empty string for unknown night role" in {
      val root = Root(
        Night(Nil, Nil, Nil, Nil),
        Day(Start = List("ds1"))
      )

      withTempJson(root) { path =>
        val narrator = new JsonNarrator(path)
        narrator.randomNightNarratorTexte("Unknown") shouldBe ""
      }
    }

    "return empty string for unknown day role" in {
      val root = Root(
        Night(Nil, Nil, Nil, Nil),
        Day(Start = List("ds1"))
      )

      withTempJson(root) { path =>
        val narrator = new JsonNarrator(path)
        narrator.randomDayNarratorTexte("Foo") shouldBe ""
      }
    }

    "not throw on empty lists (smoke test)" in {
      val root = Root(
        Night(Nil, Nil, Nil, Nil),
        Day(Start = Nil)
      )

      withTempJson(root) { path =>
        val narrator = new JsonNarrator(path)
        noException should be thrownBy narrator.randomNightNarratorTexte("Start")
        noException should be thrownBy narrator.randomDayNarratorTexte("Start")
      }
    }
  }

