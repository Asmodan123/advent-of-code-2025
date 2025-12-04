fun main() {
    fun readCommands(input: List<String>) = input.map {
        when (it.take(1)) {
            "L" -> Left(it.drop(1).toInt())
            "R" -> Right(it.drop(1).toInt())
            else -> throw IllegalArgumentException("Invalid command: $it")
        }
    }

    fun part1(input: List<String>): Int {
        val dial = Dial()
        readCommands(input).forEach { dial.rotate(it) }
        return dial.counter
    }

    fun part2(input: List<String>): Int {
        val dial = Dial()
        readCommands(input).forEach { dial.rotate2(it) }
        return dial.counter
    }

//    val testInput = readInput("Day01.input")
//    check(part1(testInput) == 3)
//    check(part2(testInput) == 6)

    val input = readInput("Day01.input")
    part1(input).println()
    part2(input).println()
}

data class Dial(var value: Int = 50, var counter: Int = 0) {
    fun rotate(command: Command) {
        require(command.value != 0)
        value = when (command) {
            is Left -> (value - command.value).mod(100)
            is Right -> (value + command.value).mod(100)
        }
        if (value == 0) counter++
    }

    fun rotate2(command: Command) {
        require(command.value != 0)
        val fullRotations = command.value / 100
        val remaining = command.value.rem(100)
        counter += fullRotations
        value = when (command) {
            is Left -> {
                if (value != 0 && value - remaining <= 0) counter++
                (value - remaining).mod(100)
            }

            is Right -> {
                if (value + remaining > 99) counter++
                (value + remaining).mod(100)
            }
        }
    }
}

sealed interface Command {
    val value: Int
}

data class Left(override val value: Int) : Command
data class Right(override val value: Int) : Command