import kotlin.math.pow
import kotlin.text.indexOf

fun main() {
    fun part1(banks: List<String>) {
        banks.sumOf { bank ->
//            maxJolatage1(bank)
            maxJolatage(bank, 2)

        }.println()
    }

    fun part2(banks: List<String>) {
        banks.sumOf { bank ->
//            maxJolatage2(bank)
            maxJolatage(bank, 12)
        }.println()
    }

//    part2(listOf("987654321111111", "811111111111119", "234234234234278", "818181911112111"))

    part1(readInput("Day03.input"))

//    part2(listOf("234234234234278"))

//    part2(readInput("Day03.input"))

}

fun maxJolatage1(bank: String): Int {
    var maxJolatage = 0
    bank.forEachIndexed { i, c ->
        for (j in i + 1 until bank.length) {
            val voltage = c.digitToInt() * 10 + bank[j].digitToInt()
            if (voltage > maxJolatage) {
                maxJolatage = voltage
            }
        }
    }
    return maxJolatage
}

fun maxJolatage(bank: String, numberOfBatteries: Int): Long {
    var maxJolatage = ""
    var subBank = bank
    for (i in 0..<numberOfBatteries) {
        val digit = findBigestDigitWithRemaining(subBank, numberOfBatteries - 1 - i)
        maxJolatage += digit
        subBank = subBank.substringAfter(digit)
    }
    return maxJolatage.toLong()
}

fun findBigestDigitWithRemaining(bank: String, ramining: Int): String {
    return bank.dropLast(ramining).maxBy { it.digitToInt() }.digitToInt().toString()
}
