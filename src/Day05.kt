fun main() {


    fun readInpunt(fileName: String): Pair<List<LongRange>, List<Long>> {
        val input = readInput(fileName)
        val ranges = mutableListOf<LongRange>();
        val ids = mutableListOf<Long>();
        var inRangesSection = true
        for (line in input) {
            if (line.isBlank()) {
                inRangesSection = false
                continue
            }
            if (inRangesSection) {
                ranges.add(line.split("-").map { it.toLong() }.let { LongRange(it.first(), it.last()) })
            } else {
                ids.add(line.toLong())
            }
        }
        return Pair(ranges, ids)
    }

    fun part1(freshIdRanges: List<LongRange>, ids: List<Long>): Int {
        return ids.count { id -> freshIdRanges.none { it.contains(id) } }
    }

    fun part1b(freshIdRanges: List<LongRange>, ids: List<Long>): Int {
        var count = 0;
        for (id in ids) {
            for (range in freshIdRanges) {
                if (range.contains(id)) {
                    count++
                    break
                }
            }
        }
        return count
    }

    fun part2(freshIdRanges: List<LongRange>): Long {
        return mergeRanges( freshIdRanges).sumOf { it.last - it.first + 1L }
    }


    val inputDemo = readInpunt("Day05.demo")
//    var resultDemo1 = part1(inputDemo.first, inputDemo.second)
//    resultDemo1.println()
//    if (resultDemo1 != 3) error("Wrong number of test cases")
//
    val input = readInpunt("Day05.input")
    //    input.println()
//    var resul1 = part1(input.first, input.second)
//    resul1.println()
//    var resul1b = part1b(input.first, input.second)
//    resul1b.println()
//    if (resul1b != 694) error("Wrong number of test cases")

    var resultDemo2 = part2(inputDemo.first)
    resultDemo2.println()
    if (resultDemo2 != 14L) error("Wrong number of test cases")
    var result2 = part2(input.first)
    result2.println()
    if (result2 != 352716206375547L) error("Wrong number of test cases")
}

fun mergeRanges(ranges: List<LongRange>): List<LongRange> {
    if (ranges.isEmpty()) return emptyList()

    val sorted = ranges.sortedBy { it.first }
    val result = mutableListOf<LongRange>()

    var current = sorted[0]

    for (next in sorted.drop(1)) {
        if (next.first <= current.last + 1) {
            // merge
            current = current.first .. maxOf(current.last, next.last)
        } else {
            result += current
            current = next
        }
    }
    result += current

    return result
}
