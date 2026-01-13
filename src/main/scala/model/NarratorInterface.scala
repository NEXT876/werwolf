// src/main/scala/model/narrator/Narrator.scala
package de.htwg.werwolf.model

trait NarratorInterface:
    def randomNightNarratorTexte(role: String): String
    def randomDayNarratorTexte(role: String): String


