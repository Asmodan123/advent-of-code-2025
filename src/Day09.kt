import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.operation.polygonize.Polygonizer
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.abs
import kotlin.math.max

fun main() {

    fun readInputField(name: String): List<Point> {
        return Path("src/$name.txt").readText().trim().lines().map { line ->
            val coords = line.split(",")
            Point(coords[0].toInt(), coords[1].toInt())
        }.toList()
    }

    var demoInput = readInputField("Day09.Demo")
    var input = readInputField("Day09.input")

    fun part1(points: List<Point>) : Long {
        val rectangles = createRectangles(points)
        rectangles.sortedByDescending { it.size }
        return rectangles[0].size
    }

    fun part2(points: List<Point>) : Long {
        val rectangles = createRectangles(points)
        val field = Field(rectangles)
        val filteredRectangles = rectangles.filter { field.isRectangleAllowed(it) }.sortedByDescending { it.size }
        val result = filteredRectangles[0]
        println(result)
        return result.size
    }

    fun part2b(points: List<Point>) : Long {
        val lineStrings = mutableListOf<LineString>()
        val gf = GeometryFactory()

        // --- horizontale Segmente ---
        val byY = points.groupBy { it.y }
        for ((_, pts) in byY) {
            val sorted = pts.sortedBy { it.x }
            for (i in 0 until sorted.size - 1) {
                val a = sorted[i]
                val b = sorted[i + 1]
                if (a.x != b.x) {
                    val p1 = Coordinate(a.x.toDouble(), a.y.toDouble())
                    val p2 = Coordinate(b.x.toDouble(), b.y.toDouble())
                    lineStrings += gf.createLineString(arrayOf(p1, p2))
                }
            }
        }

        // --- vertikale Segmente ---
        val byX = points.groupBy { it.x }
        for ((_, pts) in byX) {
            val sorted = pts.sortedBy { it.y }
            for (i in 0 until sorted.size - 1) {
                val a = sorted[i]
                val b = sorted[i + 1]
                if (a.y != b.y) {
                    val p1 = Coordinate(a.x.toDouble(), a.y.toDouble())
                    val p2 = Coordinate(b.x.toDouble(), b.y.toDouble())
                    lineStrings += gf.createLineString(arrayOf(p1, p2))
                }
            }
        }
        val polygonizer = Polygonizer()
        polygonizer.add(lineStrings)
        val polygon: Polygon = polygonizer.polygons.first() as Polygon
        val rectangles = createRectangles(points)
        val resultRectangles = rectangles
            .map { rect ->
                val rectangleEnvelope = Envelope(rect.minX().toDouble(), rect.maxX().toDouble(), rect.minY().toDouble(), rect.maxY().toDouble())
                Pair(gf.toGeometry(rectangleEnvelope), rect) }
            .filter {  rect -> polygon.contains(rect.first) }
            .filter { rect -> polygon.covers(rect.first) }
            .map { it.second }
            .toList()
            .sortedByDescending { it.size }
        val result = resultRectangles[0]
        println(result)
        return result.size
    }

//    var resultDemo = part1(demoInput)
//    println(resultDemo)
//    if (resultDemo != 50L) error("Wrong number of test cases")
//
//    var result1 = part1(input)
//    println(result1)
//    if (result1 != 4759930955L) error("Wrong number of test cases")

//    var resultDemo2 = part2b(demoInput)
//    println(resultDemo2)
//    if (resultDemo2 != 24L) error("Wrong number of test cases")

    var result2 = part2b(input)
    println(result2)
    if (result2 != 1525241870L) error("Wrong number of test cases")


}

fun createRectangles(points: List<Point>): List<Rectangle> {
    val rectangles: MutableList<Rectangle> = mutableListOf<Rectangle>()
    for (i in 0 until points.size) {
        for (j in i + 1 until points.size) {
            val point1 = points[i]
            val point2 = points[j]
            if (point1.x != point2.x && point1.y != point2.y) {
                rectangles.add(Rectangle(point1, point2))
            }
        }
    }
    return rectangles
}

class Field(rectangles: List<Rectangle>) {
    val rows: Array<MutableList<IntRange>>
    
    init {
        var height = (rectangles.maxOfOrNull { max(it.point1.y, it.point2.y) } ?: 0 ) + 2
        rows = Array(height) { mutableListOf() }
        rectangles.forEach { addRectangles(it) }
    }

    fun addRectangles(rectangle: Rectangle) {
        for (y in rectangle.yRange()) {
            var newRange = rectangle.xRange()
            val rowRanges = rows[y]
            val intersections = rowRanges.filter { it.contains(newRange.first) || it.contains(newRange.last) }
            if (intersections.isNotEmpty()) {
                val minRange = minOf(newRange.first, rowRanges.minBy { it.first }.first)
                val maxRange = maxOf(newRange.last, rowRanges.maxBy { it.last }.last)
                newRange = IntRange(minRange, maxRange)
                rowRanges.removeAll(intersections)
            }
            rowRanges.add(newRange)
        }
        println("added rectangle: $rectangle")
        println(this)
        println()
    }

    private fun isRowAllowed(y: Int, xRange: IntRange): Boolean {
        return rows[y].any { it.contains(xRange) }
    }

    fun isRectangleAllowed(rectangle: Rectangle): Boolean {
        for (y in rectangle.yRange()) {
            if (!isRowAllowed(y, rectangle.xRange())) return false
        }
        return true
    }

    override fun toString(): String {
        val width = rows.flatMap { it }.maxOf { it.last() } + 1
        return buildString {
            rows.forEach {  row ->
                row.sortedBy { it.first }
                for (x in 0..width) {
                    if (row.any { it.contains(x) }) {
                        append("X")
                    } else {
                        append(".")
                    }
                }
                append("\n")
            }
        }
    }
}

operator fun IntRange.contains(other: IntRange): Boolean =
    other.first >= this.first && other.last <= this.last

data class Point(val x: Int, val y: Int)

data class Rectangle(val point1: Point, val point2: Point) {
    val size: Long = (abs(point1.x - point2.x) + 1L) * (abs(point1.y - point2.y) + 1L)
    fun minX() = minOf(point1.x, point2.x)
    fun minY() = minOf(point1.y, point2.y)
    fun maxX() = maxOf(point1.x, point2.x)
    fun maxY() = maxOf(point1.y, point2.y)
    fun xRange() = IntRange(minX(), maxX())
    fun yRange() = IntRange(minY(), maxY())

    override fun toString(): String {
        return "(${point1.x}/${point1.y}) (${point2.x}/${point2.y})"
    }
}
