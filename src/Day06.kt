fun main() {
    fun readInpunt(fileName: String): List<List<Int>> {
        var lines = readInput(fileName)
        return lines.map { line ->
            line.split(" ")
                .filter { it.isNotEmpty() }
                .map {
                    if (it == "*") -1 else if (it == "+") -2 else it.toInt() }
                .toList()
        }
    }

    fun readInpunt2(fileName: String): Long {
        val lines = readInput(fileName).toMutableList()
        val maxLen = lines.maxOf { it.length } + 1
        val numbers = if (lines.size == 4) {
            listOf(" ".repeat(maxLen)) + lines.dropLast(1).map { it.padEnd(maxLen, ' ') }
        } else {
            lines.dropLast(1).map { it.padEnd(maxLen, ' ') }
        }
        val operatorLine = lines.last().padEnd(maxLen, ' ')
        var operator: String = "+"
        var groupSum = 0L
        var totalSum = 0L

        for (i in operatorLine.indices) {
            if (operatorLine[i] != ' ') {
                operator = operatorLine[i].toString()
                groupSum = if (operator == "+") 0L else 1L
            }

            val colNumber = "${numbers[0][i]}${numbers[1][i]}${numbers[2][i]}${numbers[3][i]}".trim()
            if (colNumber.isEmpty() ) {
                totalSum += groupSum
                groupSum = 0L
                continue
            } else {
                if (operator == "+") groupSum += colNumber.toInt() else groupSum *= colNumber.toInt()
            }
            if (i == operatorLine.lastIndex)  totalSum += groupSum

        }
        return totalSum
    }

    fun part1(input: List<List<Int>>): Long {
        var operators = input.last()
        var numbers = input.dropLast(1)
        var sum = 0L
        for (i in operators.indices) {
            var columnSum = if (operators[i] == -1) 1L else 0L
            for (j in numbers.indices) {
                if (operators[i] == -1) columnSum *= numbers[j][i] else columnSum += numbers[j][i]
            }
            val op = if (operators[i] == -1) "*" else "+"
            sum += columnSum
//            println("${numbers[0][i]} $op ${numbers[1][i]} $op ${numbers[2][i]} $op ${numbers[3][i]} = $columnSum => $sum")
        }
        return sum
    }

//    val inputDemo = readInpunt("Day06.demo")
//    val resultDemo1 = part1(inputDemo)
//    resultDemo1.println()
//    if (resultDemo1 != 4277556) error("Wrong number of test cases")

//    val input = readInpunt("Day06.input")
//    val resultPart1 = part1(input)
//    resultPart1.println()
//    if (resultPart1 != 4364617236318) error("Wrong number of test cases")

    val part2Demo = readInpunt2("Day06.demo")
    part2Demo.println()
    if (part2Demo != 3263827L) error("Wrong number of test cases")

    val part2 = readInpunt2("Day06.input")
    part2.println()
//    if (part2Demo != 3263827L) error("Wrong number of test cases")

}
