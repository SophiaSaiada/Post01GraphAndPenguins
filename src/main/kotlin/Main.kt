import arrow.core.Either
import arrow.core.flatMap
import arrow.core.identity

fun getGreatestNumberOfPenguins(
    times: List<List<Int>>,
    timeLimit: Int
): Either<String, Either<NegativeWightCycle, ResultWalk>> =
    buildGraph(times).mapLeft { it.description }.flatMap { getGreatestNumberOfPenguins(it, timeLimit) }

fun main() {
//    val times = generateRandomMatrix(3, listOf(-1, 1, 2, 3))
    val times = listOf(
        listOf(0, 1, 2, 1, -1),
        listOf(2, 0, 3, 2, 3),
        listOf(2, 2, 0, 3, 2),
        listOf(3, 3, 2, 0, 1),
        listOf(2, 1, 1, 1, 0)
    )
//    buildGraph(times).map { println(GraphVisualizer.getDotFileOf(it)) }
    val timeLimit = 5
    getGreatestNumberOfPenguins(times, timeLimit).map { negativeWeightCycleOrPath ->
        negativeWeightCycleOrPath.fold(
            ifLeft = { negativeWeightCycle ->
                """
                |All penguins can be rescued!
                |We can use this negative-weight cycle to do so: (${
                negativeWeightCycle.cycle.joinToString(", ") { (it as? AdvancedVertex)?.original?.identifier.orEmpty() }
                })
            """.trimMargin()
            },
            ifRight = { walk ->
                (
                        if (walk.numOfPenguins == times.size - 2) "All penguins can be rescued!"
                        else "Max num of penguins that can be rescued: ${walk.numOfPenguins}"
                        ) +
                        """
                        |
                        |Shortest walk to do so (in LaTeX): 
                        |${walk.vertices.windowed(2).joinToString(" ") { (start, end) ->
                            val weight =
                                walk.originalGraphEdges.find { it.start == start.original && it.end == end.original }
                                    ?.weight?.toString() ?: "-"
                            start.original.identifier + " \\xrightarrow{\\text{$weight}}"
                        } + walk.vertices.last().takeIf { walk.vertices.size > 1 }?.let { " " + it.original.identifier }.orEmpty()}
                        """.trimMargin()
            }
        )
    }.mapLeft {
        "Error: $it"
    }.fold(::identity, ::identity)
        .let(::println)
}