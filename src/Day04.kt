fun main() {
    fun part1(area: List<String>): Int {
        var numberOfRolls = 0
        for (i in area.indices) {
            var row = ""
            for (j in area[0].indices) {
                if (area[i][j] == '@') {
                    if (numberOfAdjancentRolls(i, j, area) < 4) {
                        numberOfRolls++
                        row += "x"
                    } else {
                        row += "@"
                    }
                } else {
                    row += "."
                }
            }
//            println(row)
        }
        return numberOfRolls
    }

    fun part1b(area: List<String>): Int {
        val area = Area(area)
        return area.filter { field -> field.hasRole() && field.numberOfAdjancentRolls() < 4 }.count()
    }

    fun part2b(area: List<String>): Int {
        val area = Area(area)
        var removedRolls = 0
        var removedRollsOld = -1
        while (removedRolls != removedRollsOld) {
            removedRollsOld = removedRolls
            area.filter { it.hasRole() && it.numberOfAdjancentRolls() < 4 }.forEach { field ->
                removedRolls++
                field.value = '.'
            }
        }
        return removedRolls
    }

    fun part2(area: List<String>): Int {
        var removedRolls = 0
        val area2 = area.toMutableList()
        var removedRollsOld = -1
        while (removedRolls != removedRollsOld) {
            removedRollsOld = removedRolls
            for (i in area.indices) {
                var row = ""
                for (j in area2[0].indices) {
                    if (area2[i][j] == '@') {
                        if (numberOfAdjancentRolls(i, j, area2) < 4) {
                            removedRolls++
                            area2[i] = area2[i].replaceAt(j, '.')
                            row += "x"
                        } else {
                            row += "@"
                        }
                    } else {
                        row += "."
                    }
                }
//                println(row)
            }
//            println("removedRolls: $removedRolls")
//            println()
        }
        return removedRolls
    }

    val resultDemo = part1b(readInput("Day04.demo"))
    resultDemo.println()
    assert(resultDemo == 13)

    val resultPart1 = part1b(readInput("Day04.input"))
    resultPart1.println()
    assert(resultPart1 == 1437)


    val resultDemo2 = part2b(readInput("Day04.demo"))
    resultDemo2.println()
    assert(resultDemo2 == 43)

    val resultPart2 = part2b(readInput("Day04.input"))
    resultPart2.println()
    assert(resultPart2 == 8765)
}

fun numberOfAdjancentRolls(rowIndex : Int, colIndex: Int, area: List<String>): Int {
    return Direction.entries.toTypedArray().count { hasRole(rowIndex, colIndex, it, area) }
}

fun hasRole(rowIndex: Int, colIndex: Int,  direction: Direction , area: List<String>) : Boolean {
    var newColIndex = colIndex
    var newRowIndex = rowIndex
    when(direction) {
        Direction.N -> newRowIndex--
        Direction.W -> newColIndex--
        Direction.S -> newRowIndex++
        Direction.E -> newColIndex++
        Direction.NW -> {newRowIndex--; newColIndex--}
        Direction.NE -> {newRowIndex++; newColIndex--}
        Direction.SW -> {newRowIndex--; newColIndex++}
        Direction.SE -> {newRowIndex++; newColIndex++}
    }
    if (newRowIndex < 0 || newRowIndex >= area.size || newColIndex < 0 || newColIndex >= area[0].length) return false
    return area[newRowIndex][newColIndex] == '@'
}

enum class Direction {
    N, W, S, E, NW, NE, SW, SE
}

fun String.replaceAt(index: Int, c: Char): String =
    this.take(index) + c + this.drop(index + 1)

class Area(private var rows: List<String>) {
    val field : Array<CharArray> = rows.map { it.toCharArray() }.toTypedArray()

    operator fun get(x: Int, y: Int): Char = field[x][y]
    operator fun set(x: Int, y: Int, value: Char) { field[x][y] = value }

    fun numberOfAdjancentRolls(rowIndex : Int, colIndex: Int): Int {
        return Direction.entries.toTypedArray().count { hasRole(rowIndex, colIndex, it) }
    }

    fun hasRole(rowIndex: Int, colIndex: Int,  direction: Direction) : Boolean {
        var newColIndex = colIndex
        var newRowIndex = rowIndex
        when(direction) {
            Direction.N -> newRowIndex--
            Direction.W -> newColIndex--
            Direction.S -> newRowIndex++
            Direction.E -> newColIndex++
            Direction.NW -> {newRowIndex--; newColIndex--}
            Direction.NE -> {newRowIndex++; newColIndex--}
            Direction.SW -> {newRowIndex--; newColIndex++}
            Direction.SE -> {newRowIndex++; newColIndex++}
        }
        if (newRowIndex < 0 || newRowIndex >= field.size || newColIndex < 0 || newColIndex >= field[0].size) return false
        return field[newRowIndex][newColIndex] == '@'
    }

    fun rows() = field.size

    fun applyOnEachField(action: (field: Field) -> Unit) {
        for (x in 0 until field.size) {
            for (y in 0 until field[x].size) {
                action(Field(this, x, y))
            }
        }
    }

    fun print() {
        for (row in field) println(row)
    }

    fun printSize() {
        for (x in 0 until field.size) {
            for (y in 0 until field[x].size) {
                print(numberOfAdjancentRolls(x,y))
            }
            println()
        }
    }

    data class Field(private val area: Area, val x: Int, val y: Int) {
        var value: Char
            get() = area[x, y]
            set(v) { area[x, y] = v }

        fun hasRole() = value == '@'

        fun numberOfAdjancentRolls(): Int = area.numberOfAdjancentRolls(x, y)
    }

    fun filter(filter: (Field) -> Boolean): Iterable<Field> =
        field.indices.flatMap { x ->
            field[x].indices.map { y ->
                Field(this, x, y)
            }.filter(filter)
        }
}