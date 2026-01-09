package de.htwg.werwolf.model.narratorComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import upickle.default.*
import de.htwg.werwolf.model.narrator.{Night, Root}

class NarratorModelSpec extends AnyWordSpec with Matchers {

  "Night and Root" should {

    "be serializable and deserializable via uPickle (round-trip)" in {
      val original = Root(
        Night = Night(
          Start = List("Alice", "Bob"),
          Werwolf = List("Charlie"),
          Amor = List("Dave", "Eve"),
          Witch = List("Frank")
        )
      )

      // Serialize to JSON
      val json = write(original)

      // Expected JSON structure (pretty-printed for clarity)
      val expectedJson =
        """{
          "Night": {
            "Start": ["Alice", "Bob"],
            "Werwolf": ["Charlie"],
            "Amor": ["Dave", "Eve"],
            "Witch": ["Frank"]
          }
        }""".stripMargin.replaceAll("\\s", "")

      // Remove whitespace for comparison
      json.replaceAll("\\s", "") should include(expectedJson.replaceAll("\\s", ""))

      // Deserialize back
      val deserialized = read[Root](json)

      deserialized shouldEqual original
    }

    "handle empty lists correctly" in {
      val empty = Root(Night(List(), List(), List(), List()))
      val json = write(empty)
      val back = read[Root](json)

      back shouldEqual empty
    }

    "fail gracefully on invalid JSON" in {
      val invalidJson = """{"Night": {"Start": "not a list"}}"""

      an [Exception] should be thrownBy {
        read[Root](invalidJson)
      }
    }
  }
}