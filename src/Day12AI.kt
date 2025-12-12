import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private data class Shape(val id: Int, val cells: List<Pair<Int, Int>>)
private data class Region(val w: Int, val h: Int, val qty: IntArray)

fun main2(args: Array<String>) {
    val path = args.firstOrNull() ?: "src/Day12.input.txt"
    val text = File(path).readText()
    val (shapes, regions) = parseInput(text)

    require(shapes.isNotEmpty()) { "Keine Shapes gefunden." }
    require(regions.isNotEmpty()) { "Keine Regionen gefunden." }

    // nur Rotationen, keine Spiegelung
    val variantsById = shapes.associate { it.id to allRotationsNoFlip(it) }

    var count = 0
    regions.forEachIndexed { idx, r ->
        println("Prüfe Region ${idx + 1}/${regions.size}: ${r.w}x${r.h} ...")
        val ok = canPackExactDLXAllowHoles(r, variantsById)
        println(" -> ${if (ok) "PACKBAR" else "NICHT packbar"}")
        if (ok) count++
    }
    println(count)
}

fun main(args: Array<String>) {
    val path = args.firstOrNull() ?: "src/Day12.input.txt"
    val text = File(path).readText()
    val (shapes, regions) = parseInput(text)

    val variantsById = shapes.associate { it.id to allRotationsNoFlip(it) }

    // CPU-bound -> Threads ≈ Kerne
    val threads = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
    val pool = Executors.newFixedThreadPool(threads)

    try {
        val tasks = regions.mapIndexed { idx, r ->
            Callable {
                val ok = canPackExactDLXAllowHoles(r, variantsById)
                idx to ok
            }
        }

        val futures = pool.invokeAll(tasks)

        val results = BooleanArray(regions.size)
        for (f in futures) {
            val (idx, ok) = f.get()
            results[idx] = ok
        }

        // Ausgabe in Originalreihenfolge
        var count = 0
        for (i in regions.indices) {
            val r = regions[i]
            val ok = results[i]
            println("Region ${i + 1}/${regions.size} ${r.w}x${r.h}: ${if (ok) "PACKBAR" else "NICHT packbar"}")
            if (ok) count++
        }
        println(count)
    } finally {
        pool.shutdown()
    }
}

/* ---------------- Parsing ---------------- */

private fun parseInput(text: String): Pair<List<Shape>, List<Region>> {
    val lines = text.lines().map { it.trimEnd() }
    val shapes = mutableListOf<Shape>()
    val regions = mutableListOf<Region>()

    var i = 0
    while (i < lines.size) {
        val line = lines[i].trim()
        if (line.isEmpty()) { i++; continue }

        val shapeMatch = Regex("""^(\d+):$""").matchEntire(line)
        if (shapeMatch != null) {
            val id = shapeMatch.groupValues[1].toInt()
            i++
            val grid = mutableListOf<String>()
            while (i < lines.size) {
                val t = lines[i].trim()
                if (t.isEmpty()) break
                if (Regex("""^\d+:\s*$""").matches(t)) break
                if (Regex("""^\d+x\d+:""").containsMatchIn(t)) break
                grid.add(t)
                i++
            }
            val cells = mutableListOf<Pair<Int, Int>>()
            for (y in grid.indices) {
                for (x in grid[y].indices) if (grid[y][x] == '#') cells.add(x to y)
            }
            require(cells.isNotEmpty()) { "Shape $id hat keine # Zellen." }
            shapes.add(normalizeShape(Shape(id, cells)))
            continue
        }

        val regionMatch = Regex("""^(\d+)x(\d+):\s*(.*)$""").matchEntire(line)
        if (regionMatch != null) {
            val w = regionMatch.groupValues[1].toInt()
            val h = regionMatch.groupValues[2].toInt()
            val nums = regionMatch.groupValues[3]
                .trim()
                .split(Regex("""\s+"""))
                .filter { it.isNotEmpty() }
                .map { it.toInt() }
                .toIntArray()
            regions.add(Region(w, h, nums))
            i++
            continue
        }

        i++
    }

    val sortedShapes = shapes.sortedBy { it.id }
    for (k in sortedShapes.indices) {
        require(sortedShapes[k].id == k) { "Shape-Indizes müssen bei 0 beginnen und lückenlos sein." }
    }
    return sortedShapes to regions
}

/* ---------------- Shape transforms (NO FLIP) ---------------- */

private fun normalizeShape(s: Shape): Shape {
    val minX = s.cells.minOf { it.first }
    val minY = s.cells.minOf { it.second }
    val norm = s.cells
        .map { (x, y) -> (x - minX) to (y - minY) }
        .sortedWith(compareBy({ it.second }, { it.first }))
    return Shape(s.id, norm)
}

private fun rotate90(s: Shape): Shape {
    // (x, y) -> (y, -x), dann normalisieren
    val rot = s.cells.map { (x, y) -> y to -x }
    return normalizeShape(Shape(s.id, rot))
}

private fun allRotationsNoFlip(s: Shape): List<Shape> {
    val seen = HashSet<List<Pair<Int, Int>>>()
    val out = ArrayList<Shape>(4)
    var cur = normalizeShape(s)
    repeat(4) {
        if (seen.add(cur.cells)) out.add(cur)
        cur = rotate90(cur)
    }
    return out
}

/* ---------------- DLX with primary/secondary columns ---------------- */

private class DLX(private val primaryCount: Int, private val totalCount: Int) {

    private open inner class Node {
        lateinit var L: Node
        lateinit var R: Node
        lateinit var U: Node
        lateinit var D: Node
        lateinit var C: Column
        var rowId: Int = -1
    }

    private inner class Column(val name: Int, val isPrimary: Boolean) : Node() {
        var size: Int = 0
    }

    private val header = Column(-1, true)
    private val columns = Array(totalCount) { idx ->
        Column(idx, idx < primaryCount)
    }

    init {
        // Header contains ONLY primary columns.
        header.L = header
        header.R = header
        header.U = header
        header.D = header
        header.C = header

        var prev: Node = header
        for (c in columns) {
            // vertical self-loop
            c.U = c
            c.D = c
            c.C = c
            c.size = 0

            if (c.isPrimary) {
                // horizontal link into primary list
                c.L = prev
                c.R = header
                prev.R = c
                header.L = c
                prev = c
            } else {
                // secondary columns are NOT in header list
                c.L = c
                c.R = c
            }
        }
    }

    fun addRow(rowId: Int, colIndices: IntArray) {
        require(colIndices.isNotEmpty())
        var first: Node? = null
        var prev: Node? = null

        for (ci in colIndices) {
            val col = columns[ci]
            val n = Node()
            n.C = col
            n.rowId = rowId

            // link into column (before col)
            n.D = col
            n.U = col.U
            col.U.D = n
            col.U = n
            col.size++

            // link into row circularly
            if (first == null) {
                first = n
                n.L = n
                n.R = n
                prev = n
            } else {
                n.L = prev!!
                n.R = first
                prev!!.R = n
                first.L = n
                prev = n
            }
        }
    }

    private fun cover(c: Column) {
        // remove column from header only if primary
        if (c.isPrimary) {
            c.R.L = c.L
            c.L.R = c.R
        }
        var i = c.D
        while (i !== c) {
            var j = i.R
            while (j !== i) {
                j.D.U = j.U
                j.U.D = j.D
                j.C.size--
                j = j.R
            }
            i = i.D
        }
    }

    private fun uncover(c: Column) {
        var i = c.U
        while (i !== c) {
            var j = i.L
            while (j !== i) {
                j.C.size++
                j.D.U = j
                j.U.D = j
                j = j.L
            }
            i = i.U
        }
        if (c.isPrimary) {
            c.R.L = c
            c.L.R = c
        }
    }

    private fun choosePrimaryColumn(): Column? {
        var best: Column? = null
        var minSize = Int.MAX_VALUE
        var c = header.R
        while (c !== header) {
            val col = c as Column
            if (col.size < minSize) {
                minSize = col.size
                best = col
                if (minSize == 0) return best
                if (minSize == 1) return best
            }
            c = c.R
        }
        return best
    }

    fun solveExists(): Boolean = search()

    private fun search(): Boolean {
        // solved when all PRIMARY columns are covered
        if (header.R === header) return true

        val c = choosePrimaryColumn() ?: return false
        if (c.size == 0) return false

        cover(c)
        var r = c.D
        while (r !== c) {
            var j = r.R
            while (j !== r) {
                cover(j.C)
                j = j.R
            }

            if (search()) return true

            j = r.L
            while (j !== r) {
                uncover(j.C)
                j = j.L
            }
            r = r.D
        }
        uncover(c)
        return false
    }
}

/* ---------------- Packing model: pieces exact, cells at-most-one ---------------- */

private fun canPackExactDLXAllowHoles(
    region: Region,
    variantsById: Map<Int, List<Shape>>
): Boolean {
    val w = region.w
    val h = region.h
    val cellCount = w * h
    val shapeCount = region.qty.size

    // notwendiger Check: Gesamtfläche der Teile darf nicht größer als Board sein
    var needed = 0
    for (sid in 0 until shapeCount) {
        val area = variantsById[sid]?.firstOrNull()?.cells?.size
            ?: error("Keine Varianten für Shape $sid")
        needed += area * region.qty[sid]
    }
    if (needed > cellCount) return false

    // piece instances: jede Instanz MUSS genau einmal gewählt werden (primäre Spalten)
    val pieces = ArrayList<Int>(region.qty.sum())
    for (sid in 0 until shapeCount) repeat(region.qty[sid]) { pieces.add(sid) }

    val primaryCount = pieces.size
    val totalCols = primaryCount + cellCount

    val dlx = DLX(primaryCount = primaryCount, totalCount = totalCols)

    // Row = konkrete Platzierung einer konkreten Instanz:
    // - deckt genau 1 piece-column (Instanz)
    // - deckt mehrere cell-columns (alle belegten Zellen)
    var rowId = 0
    pieces.forEachIndexed { pIndex, shapeId ->
        val vars = variantsById.getValue(shapeId)

        for (v in vars) {
            val maxX = v.cells.maxOf { it.first }
            val maxY = v.cells.maxOf { it.second }

            for (oy in 0 until (h - maxY)) {
                for (ox in 0 until (w - maxX)) {
                    val cols = IntArray(1 + v.cells.size)
                    cols[0] = pIndex // primary: piece instance

                    var t = 1
                    for ((cx, cy) in v.cells) {
                        val x = ox + cx
                        val y = oy + cy
                        val cellCol = primaryCount + (y * w + x) // secondary
                        cols[t++] = cellCol
                    }

                    dlx.addRow(rowId++, cols)
                }
            }
        }
    }

    return dlx.solveExists()
}