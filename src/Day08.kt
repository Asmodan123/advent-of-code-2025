import kotlin.collections.filter
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.sortByDescending
import kotlin.collections.sortedBy
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {

    fun readInputFile3(fileName: String): List<Edge> {
        val edges = mutableListOf<Edge>()
        val input = readInput(fileName)
        val points = mutableListOf<Point3D>()
        input.forEachIndexed { index, s -> points.add(Point3D(index, s.split(",").map { it.toInt() }.toIntArray())) }
        for (i in 0 until points.size) {
            for (j in i + 1 until points.size) {
                edges.add(Edge(points[i], points[j], distance(points[i], points[j])))
            }
        }
        return edges.sortedBy { it.distance }
    }

    val inputDemo = readInputFile3("Day08.demo")
    val imput = readInputFile3("Day08.input")

//    val resultDemo = part1(inputDemo, 10)
//    println(resultDemo)
//    if (resultDemo != 40) error("Wrong number of test cases")
//
//    val result1 = part1(imput, 1000)
//    println(result1)
//    if (result1 != 50760) error("Wrong number of test cases")

//    val resultDemo2 = part2(inputDemo)
//    println(resultDemo2)
//    if (resultDemo2 != 25272) error("Wrong number of test cases")

    val result2 = part2(imput)
    println(result2)
    if (result2 != 50760L) error("Wrong number of test cases")

}

fun part1(input: List<Edge>, runs: Int): Int {
    val circuits: MutableList<Circuit> = mutableListOf()
    val edges: MutableList<Edge> = input.sortedBy { it.distance }.toMutableList()
    repeat(runs) {
        val edge = edges.removeFirst()

        var action = "skipped"
        val relatedCircuits = circuits.filter { circuit -> circuit.relatesTo(edge) }
        if (relatedCircuits.isEmpty()) {
            circuits.add(Circuit().add(edge))
            action = "added to new circuit"
        } else if (relatedCircuits.size == 1) {
            val circuit = relatedCircuits.first()
            if (circuit.canConnecct(edge)) {
                action = "added to existing circuit $circuit"
                circuit.add(edge)
            }
        } else if (relatedCircuits.size == 2) {
            val circuit1 = relatedCircuits.first()
            val circuit2 = relatedCircuits.last()
            if (circuit1.canConnecct(edge) && circuit2.canConnecct(edge)) {
                action = "joined circuits $circuit1 and $circuit2 and added to"
                circuit1.add(edge)
                circuit1.join(circuit2)
                circuits.remove(circuit2)
            }
        } else {
            error("ERROR: ${relatedCircuits.size} related circuits")
        }
//        println("$edge ${edge.point1} ${edge.point2} ($action)")
//        println(circuits.joinToString("\n"))
    }

    circuits.sortByDescending { it.size() }
    if (circuits.size > 3) {
        return circuits[0].size() * circuits[1].size() * circuits[2].size()
    }
    return 0
}

fun part2(input: List<Edge>): Long {
    val circuits: MutableList<Circuit> = mutableListOf()
    val pointsCount = (input.map { it.point1 }.toSet() + input.map { it.point2 }.toSet()).count()

    val edges: MutableList<Edge> = input.sortedBy { it.distance }.toMutableList()
    var currentEdge: Edge?
    do {
        currentEdge = edges.removeFirst()

        var action = "skipped"
        val relatedCircuits = circuits.filter { circuit -> circuit.relatesTo(currentEdge) }
        if (relatedCircuits.isEmpty()) {
            circuits.add(Circuit().add(currentEdge))
            action = "added to new circuit"
        } else if (relatedCircuits.size == 1) {
            val circuit = relatedCircuits.first()
            if (circuit.canConnecct(currentEdge)) {
                action = "added to existing circuit $circuit"
                circuit.add(currentEdge)
            }
        } else if (relatedCircuits.size == 2) {
            val circuit1 = relatedCircuits.first()
            val circuit2 = relatedCircuits.last()
            if (circuit1.canConnecct(currentEdge) && circuit2.canConnecct(currentEdge)) {
                action = "joined circuits $circuit1 and $circuit2 and added to"
                circuit1.add(currentEdge)
                circuit1.join(circuit2)
                circuits.remove(circuit2)
            }
        } else {
            error("ERROR: ${relatedCircuits.size} related circuits")
        }
//        println("$currentEdge ${currentEdge.point1} ${currentEdge.point2} ($action)")
//        println(circuits.joinToString("\n"))
//        println()

    } while (circuits[0].size() != pointsCount)
    println("$currentEdge (${currentEdge.point1} / ${currentEdge.point2}")
    return currentEdge.point1.x.toLong() * currentEdge.point2.x.toLong()
}


class Circuit() {
    val points: MutableSet<Point3D> = mutableSetOf()
    fun add(newEdge: Edge): Circuit {
        points.add(newEdge.point1)
        points.add(newEdge.point2)
        return this
    }

    fun relatesTo(edge: Edge): Boolean {
        return points.contains(edge.point1) || points.contains(edge.point2)
    }

    fun canConnecct(edge: Edge): Boolean {
        return points.contains(edge.point1) xor points.contains(edge.point2)
    }

    fun size() = points.size

    fun join(circuit2join: Circuit) {
        points.addAll(circuit2join.points)
    }

    override fun toString(): String {
        return "Circuit (${size()}): [${points.map { it.id }.joinToString()}]"
    }
}

data class Edge(val point1: Point3D, val point2: Point3D, val distance: Double) {
    override fun toString(): String {
        return "${point1.id} <--> ${point2.id} ($distance)"
    }
}

data class Point3D(val id: Int, val x: Int, val y: Int, val z: Int) {
    constructor(id: Int, points: IntArray) : this(id, points[0], points[1], points[2])

    override fun toString(): String {
        return "$id: [$x,$y,$z]"
    }
}

fun distance(a: Point3D, b: Point3D): Double =
    sqrt((a.x - b.x).toDouble().pow(2) +
         (a.y - b.y).toDouble().pow(2) +
         (a.z - b.z).toDouble().pow(2)
    )



