import scala.io.StdIn.readLine

@main
def ttt() : Unit =
    val scale = readIntWithDefault("Bitte die größe des Spielfeldes eingeben, die default Größe ist 3 mal 3 ( für default enter drücken ): ", 3);
    val groeße = readIntWithDefault("Bitte die größe der einzelnen Felder eingeben, die default Größe ist 4 ( für default enter drücken ): ", 4);
   
    for i <- 1 to scale do
        println("+" + ("-" * groeße + "+") * scale)
        print((("|" + " " * groeße) * scale +"|\n") * (groeße / 4))
        
    println("+" + ("-" * groeße + "+") * scale)


def readIntWithDefault(prompt:String, default: Int): Int =
    val input = readLine(prompt)
    if input.isEmpty then default else input.toInt


