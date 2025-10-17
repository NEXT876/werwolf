import scala.io.StdIn.readLine

//TTT Feld zum skalieren testen
@main
def TTT() : Unit =
    val scale = readIntWithDefault("Bitte die größe des Spielfeldes eingeben, die default Größe ist 3 mal 3 ( für default enter drücken ): ", 3);
    val groeße = readIntWithDefault("Bitte die größe der einzelnen Felder eingeben, die default Größe ist 4 ( für default enter drücken ): ", 4);
  
    println(TTTField(scale,groeße))

def TTTField(scale : Int, groeße : Int) : String =
    val gamefield = new StringBuilder
    for i <- 1 to scale do
        gamefield.append("+" + ("-" * groeße + "+") * scale + "\n")
        gamefield.append((("|" + " " * groeße) * scale +"|\n") * (groeße / 4))
    gamefield.append("+" + ("-" * groeße + "+") * scale).toString()


def readIntWithDefault(prompt:String, default: Int): Int =
    val input = readLine(prompt)
    if input.isEmpty then default else input.toInt



