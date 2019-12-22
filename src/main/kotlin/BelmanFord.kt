fun belmanFord(graph: Graph, start: Vertex): Map<Vertex, Int> {
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

    graph.edges.forEach { edge ->
        distances[edge.start]?.let { edgeStartDistance ->
            val edgeEndDistance = distances[edge.end]
            if (edgeEndDistance == null || edgeStartDistance + edge.weight < edgeEndDistance) {
                throw RuntimeException("Graph contains a negative-weight cycle")
            }
        }
    }

    return distances.filterNotNullValues()
}

