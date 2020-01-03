import arrow.core.Either
import arrow.core.toOption

fun belmanFord(graph: Graph, start: Vertex): Either<NegativeWightCycle, BelmanFordResult> {
    val distances: MutableMap<Vertex, Int?> =
        graph.vertices.map {
            it to if (it == start) 0 else null
        }.toMap().toMutableMap()
    val predecessors: MutableMap<Vertex, Vertex?> =
        graph.vertices.map {
            it to null
        }.toMap().toMutableMap()

    repeat(graph.vertices.size - 1) {
        graph.edges.forEach { edge ->
            distances[edge.start]?.let { edgeStartDistance ->
                val edgeEndDistance = distances[edge.end]
                if (edgeEndDistance == null || edgeStartDistance + edge.weight < edgeEndDistance) {
                    distances[edge.end] = edgeStartDistance + edge.weight
                    predecessors[edge.end] = edge.start
                }
            }
        }
    }

    val negativeWightCycle =
        graph.edges.asSequence().mapNotNull { edge ->
            distances[edge.start]?.let { edgeStartDistance ->
                val edgeEndDistance = distances[edge.end]
                if (edgeEndDistance == null || edgeStartDistance + edge.weight < edgeEndDistance) {
                    distances[edge.end] = edgeStartDistance + edge.weight
                    predecessors[edge.end] = edge.start
                    if (edge.start == edge.end)
                        listOf(edge.end)
                    else
                        path(from = edge.end, to = edge.start, using = predecessors.filterNotNullValues()) + edge.end
                } else null
            }
        }.firstOrNull().toOption()

    return negativeWightCycle.map(::NegativeWightCycle).toEither {
        BelmanFordResult(
            start,
            distances.filterNotNullValues(),
            predecessors.filterNotNullValues()
        )
    }.swap()
}

data class NegativeWightCycle(
    val cycle: List<Vertex>
)

data class BelmanFordResult(
    val start: Vertex,
    val distances: Map<Vertex, Int>,
    private val predecessors: Map<Vertex, Vertex>
) {
    fun pathFromStartTo(vertex: Vertex): List<Vertex> =
        path(from = start, to = vertex, using = predecessors)
}

fun path(from: Vertex, to: Vertex, using: Map<Vertex, Vertex>): List<Vertex> =
    if (to == from) listOf(from)
    else path(from, using.getValue(to), using) + to
