import java.util.BitSet
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlinx.coroutines.*
import kotlin.collections.map


fun main() {
    fun readInput(fileName: String):List<Machine> {
        return Path("src/$fileName.txt").readText().trim().lines().map { line -> Machine(line) }.toList()
    }

    val inputDemo = readInput("Day10.demo")
    val input = readInput("Day10.input")

    fun part1(machines: List<Machine>): Int {
        return machines.sumOf { turnOn(it) }
    }

    suspend fun part1b(machines: List<Machine>): Int = coroutineScope {
        machines
            .map { m -> async(Dispatchers.Default) { turnOn2(m) } }
            .awaitAll()
            .sum()
    }

    fun part1c(machines: List<Machine>): Int {
        return machines.sumOf { turnOn3(it) }
    }

//    val resut1Demo = part1(inputDemo)
//    println(resut1Demo)
//    if (resut1Demo != 7) error("Wrong number of test cases")

    val resut1 = part1c(input)
    println(resut1)
    if (resut1 != 522) error("Wrong number of test cases")

}

fun permute(list: List<BitSet>): Sequence<List<BitSet>> = sequence {
    if (list.size == 1) {
        yield(list)
    } else {
        for (i in list.indices) {
            val element = list[i]
            val rest = list.take(i) + list.drop(i + 1)
            for (perm in permute(rest)) {
                yield(listOf(element) + perm)
            }
        }
    }
}

fun getSubsets(list: List<BitSet>): List<List<BitSet>> {
    val n = list.size
    // Die Anzahl der Teilmengen ist 2 hoch n (z.B. 1 shl 3 = 8)
    val subsetCount = 1 shl n

    val allSubsets = mutableListOf<List<BitSet>>()

    for (i in 0 until subsetCount) {
        val subset = mutableListOf<BitSet>()
        for (j in 0 until n) {
            // Prüfen, ob das j-te Bit in der Zahl i gesetzt ist
            if ((i and (1 shl j)) > 0) {
                subset.add(list[j])
            }
        }
        allSubsets.add(subset)
    }
    return allSubsets
}

fun turnOn(machine: Machine): Int {
//    println("Turn on maschine $maschine")
    var count = Int.MAX_VALUE
    repeat(200) loop@ {
        val newCount = pushButtons(machine.lightPanel, machine.buttons.shuffled(), machine, 0, count)
        if (newCount < count) {
            count = newCount
        }
        if (newCount == 1) {
            return@loop
        }
    }
    if (count == -1) {
        println("No solution found for maschine: $machine")
        return 0
    }
    println("Turned on maschine: $machine with $count steps\n")
    return count
}



fun turnOn2(machine: Machine): Int {
//    println("Turn on maschine $maschine")
    var buttonCount = Int.MAX_VALUE
    var permutationCount = 0
    for (buttons in permute(machine.buttons)) {
        permutationCount++
        val newCount = pushButtons(machine.lightPanel, buttons, machine, 0, buttonCount)
        if (newCount < buttonCount) {
            buttonCount = newCount
        }
        if (newCount == 1) {
            break
        }
    }
    if (buttonCount == -1) {
        println("No solution found after $permutationCount permutations for maschine $machine")
        return 0
    }
    println("Turned on maschine: $machine with $buttonCount steps after $permutationCount permutations\n")
    return buttonCount
}


fun turnOn3(machine: Machine): Int {
//    println("Turn on maschine $maschine")
    var minSubSet: List<BitSet> = emptyList()
    for (subset in getSubsets(machine.buttons)) {
//        println("Try ButtonSubSet $subset")
        val lightPanel = BitSet()
        for (button in subset) {
            lightPanel.xor(button)
        }
        if (machine.finalLightPanel == lightPanel && (minSubSet.isEmpty()  || subset.size < minSubSet.size)) {
            minSubSet = subset
        }
    }
    println("Turned on maschine: $machine with ${minSubSet.size} ButtonSequence ${minSubSet}\n")

    return minSubSet.size
}

fun pushButtons(lightPanel: BitSet, buttons: List<BitSet>, machine: Machine, counter: Int, currentBestCounter: Int): Int {
//    println("Push buttons $buttons on panel $lightPanel (counter: $counter)")
    for (button in buttons) {
        val currentLightPanel = lightPanel.clone() as BitSet
        if (pushButton(currentLightPanel, button, machine)) {
            return counter + 1
        } else if (counter < currentBestCounter) {
            val newButtons = buttons.toMutableList()
            newButtons.remove(button)
            val newCounter = pushButtons(currentLightPanel, newButtons, machine, counter + 1, currentBestCounter)
            if (newCounter != -1) {
                return newCounter
            }
        }
    }
    return -1
}

fun pushButton(lightPanel: BitSet, button: BitSet, machine: Machine): Boolean {
//    println("Push button $button on panel $lightPanel")
    lightPanel.xor(button)
    return machine.finalLightPanel == lightPanel
}

data class Machine(private val machineString: String) {
    val finalLightPanel: BitSet
    var lightPanel: BitSet = BitSet()
    val buttons: List<BitSet>

    init {
        finalLightPanel = getLightPanel(machineString)
        buttons = getButtons(machineString)
    }

    private fun getLightPanel(s: String): BitSet {
        val inner = Regex("""\[(.*?)]""").find(s)!!.groupValues[1]
        val bitSet = BitSet()
        inner.forEachIndexed { idx, c ->
            if (c == '#') bitSet.set(idx)
        }
        return bitSet
    }

    private fun getButtons(s: String): List<BitSet> {
        val regex = Regex("""\((.*?)\)""")
        return regex.findAll(s).map { m ->
            val bitSet = BitSet()
            m.groupValues[1]
                .split(',')
                .map { it.trim().toInt() }
                .forEach { bitSet.set(it) }
            bitSet
        }.toList()
    }

}