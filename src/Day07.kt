fun main() {
    fun readFileAs2DCharArray(filename: String): Array<CharArray> {
        val lines = readInput(filename)
        val cols = lines.maxOfOrNull { it.length } ?: 0

        return lines.map { line ->
            line.padEnd(cols).toCharArray()
        }.toTypedArray()
    }

    fun part1(input: Array<CharArray>): Long {
        var counSplit = 0L
        for (i in 1 until input.size ) {
            print(input)
            println()
            for (j in input[i].indices) {
                if (input[i-1][j] == '|' || input[i-1][j] == 'S') {
                    if (input[i][j] == '^') {
                        counSplit++
                        if (j > 0)
                        input[i][j-1] = '|'
                        if (j < input[i].size - 1)
                            input[i][j+1] = '|'
                    } else {

                        input[i][j] = '|'
                    }
                }
            }
        }
        return counSplit
    }

    fun part2(input: Array<CharArray>): Long {
        var worldCount = 1L
        var priorLines : MutableList<CharArray> = mutableListOf(input[0])
        var nextLines : MutableList<CharArray> = mutableListOf()
        for (i in 1 until input.size ) {
            nextLines.clear()
            for (priorLine in priorLines) {
                for (j in priorLine.indices) {
                    if (priorLine[j] == '|' || priorLine[j] == 'S') {
                        if (input[i][j] == '^') {
                            worldCount++
                            if (j > 0) {
                                val newLine = input[i].clone()
                                newLine[j-1] = '|'
                                nextLines.add(newLine)
                            }
                            if (j < priorLine.size - 1) {
                                val newLine = input[i].clone()
                                newLine[j+1] = '|'
                                nextLines.add(newLine)
                            }
                        } else {
                            val newLine = input[i].clone()
                            newLine[j] = '|'
                            nextLines.add(newLine)
                        }
                    }
                }
            }
            priorLines.clear()
            priorLines.addAll(nextLines)
//            priorLines.forEach { print("${it.joinToString("")} ") }
//            println(worldCount)
        }
        return worldCount
    }


    fun worldCount(input: Array<CharArray>, row: Int, pos: Int): Long {
        if (row >= input.size -1) return 1L
        if (input[row+1][pos] == '^') {
            return worldCount(input, row+1, pos -1) + worldCount(input, row+1, pos +1)

        } else {
            return worldCount(input, row+1, pos)
        }
    }

    fun part2Recursive(input: Array<CharArray>): Long {
        input[0].indexOf('S')
        return worldCount(input, 1, input[0].indexOf('S'))
    }

    fun worldCountBottomUp(input: Array<CharArray>, startPos: Int): Long {
        val rows = input.size
        val cols = input[0].size

        var currentWorldCountLine = LongArray(cols)
        currentWorldCountLine.fill(1L)
        var priorWorldCountLine = LongArray(cols)

        for (row in rows - 2 downTo 0) {
            priorWorldCountLine.fill(0)

            for (pos in 0 until cols) {
                val charBelow = input[row + 1][pos]
                priorWorldCountLine[pos] =
                    if (charBelow == '^') {
                        (if (pos > 0) currentWorldCountLine[pos - 1] else 0L) +
                        (if (pos < cols - 1) currentWorldCountLine[pos + 1] else 0L)
                    } else {
                        currentWorldCountLine[pos]
                    }
            }

            val tmp = currentWorldCountLine
            currentWorldCountLine = priorWorldCountLine
            priorWorldCountLine = tmp
        }

        return currentWorldCountLine[startPos]
    }

    fun part2BottomUp(input: Array<CharArray>): Long {
        return worldCountBottomUp(input,input[0].indexOf('S'))
    }

        var inputDemo = readFileAs2DCharArray("Day07.demo")
//    var resultDemo = part1(inputDemo)
//    println(resultDemo)
//    if (resultDemo != 21L) error("Wrong number of test cases")
//
    var input = readFileAs2DCharArray("Day07.input")
//    var result1 = part1(input)
//    println(result1)
//    if (resultDemo != 1594L) error("Wrong number of test cases")

    var resultDemo2 = part2BottomUp(inputDemo)
    println(resultDemo2)
    if (resultDemo2 != 40L) error("Wrong number of test cases")


    var result2 = part2BottomUp(input)
    println(result2)
    if (result2 != 15650261281478L) error("Wrong number of test cases")

}

fun print(input: Array<CharArray>) {
    input.forEach { println(it.joinToString("")) }
}