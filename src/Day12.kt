import com.sun.tools.javac.code.TypeAnnotationPosition.field
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    fun readInput(fileName: String): Pair<List<Fliese>, List<Feld>> {
        val fliesen = mutableListOf<Fliese>()
        val felder = mutableListOf<Feld>()
        val lines = Path("src/$fileName.txt").readText().trim().lines()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.isEmpty()) {
                i++
            } else  if (line[1] == ':') {
                val fliesenId = line[0].digitToInt()
                val size = lines[i + 1].count { it == '#' } + lines[i + 2].count { it == '#' } + lines[i + 3].count { it == '#' }
                fliesen.add(Fliese(fliesenId, size))
                i += 4
            } else {
                val parts = line.split(":")
                val size = parts[0].split("x")
                val fliesenMengen = parts[1].trim().split(" ").map { it.toInt() }.toList()
                felder.add(Feld(i, size[0].toInt(), size[1].toInt(), fliesenMengen))
                i++
            }

        }
        return Pair(fliesen, felder)
    }

    fun part1(fliesen: List<Fliese>, felder: List<Feld>): Int {
        for (feld in felder) {
            val feldSize = feld.width * feld.height
            var fliesenMengenSize = 0
            feld.fliesenMengen.forEachIndexed { index, i ->
                fliesenMengenSize += fliesen[index].size * i
            }
            if (fliesenMengenSize <= feldSize) {
                println("$feldSize $fliesenMengenSize")
            }
        }
        return 0
    }

//    val (fliesen, felder) = readInput("Day12.demo")
    val (fliesen, felder) = readInput("Day12.input")
    println(fliesen)
    println(felder)
    part1(fliesen, felder)
}

data class Fliese(val id: Int, val size: Int) {


}

data class Feld(val id: Int, val width: Int, val height: Int, val fliesenMengen: List<Int>) {

}
