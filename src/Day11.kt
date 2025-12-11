import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    fun readInput(fileName: String): Graph<String, DefaultEdge> {
        val graph: Graph<String, DefaultEdge> =
            DefaultDirectedGraph(DefaultEdge::class.java)
        Path("src/$fileName.txt").readText().trim().lines().forEach { line ->
            val node = line.take(3)
            if (!graph.containsVertex(node)) {
                graph.addVertex(node)
            }
            line.substring(5).split(" ").forEach { neighbor ->
                if (!graph.containsVertex(neighbor)) {
                    graph.addVertex(neighbor)
                }
                graph.addEdge(node, neighbor)
            }
        }
        return graph
    }

    val inputDemo = readInput("Day11.demo")
    val inputDemo2 = readInput("Day11.demo2")
    val input = readInput("Day11.input")

    fun part1(graph: Graph<String, DefaultEdge>): Int {
        val allPathsAlg = AllDirectedPaths(graph)
        val paths = allPathsAlg.getAllPaths("you", "out", true,null)
        return paths.size
    }

    fun part2(graph: Graph<String, DefaultEdge>): Long {
        val allPathsAlg = AllDirectedPaths(graph)
        val paths_SVR_FFT = allPathsAlg.getAllPaths("svr","fft",true,null)
        println("Found ${paths_SVR_FFT.size} paths from svr to fft")

        val paths_FFT_DAC = allPathsAlg.getAllPaths("fft","dac",true,null)
        println("Found ${paths_FFT_DAC.size} paths from fft to dac")

        val paths_DAC_OUT = allPathsAlg.getAllPaths("dac","out",true,null)
        println("Found ${paths_DAC_OUT.size} paths from dac to out")

        return paths_SVR_FFT.size.toLong() * paths_FFT_DAC.size * paths_DAC_OUT.size
    }

//    val result1Demo = part1(inputDemo)
//    println(result1Demo)
//    if (result1Demo != 5) error("Wrong number of test cases")
//
//    val result1 = part1(input)
//    println(result1)
//    if (result1 != 649) error("Wrong number of test cases")

//    val result2Demo = part2(inputDemo2)
//    println(result2Demo)
//    if (result2Demo != 2) error("Wrong number of test cases")

    val result2 = part2(input)
    println(result2)
    if (result2 != 458948453421420L) error("Wrong number of test cases")

}
