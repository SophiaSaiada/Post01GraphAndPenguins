import arrow.core.*


data class ResultWalk(
    val vertices: List<AdvancedVertex>,
    val originalGraphEdges: Set<Edge>
) {
    val numOfPenguins = vertices.count { it.identifier.startsWith("P") }
}

fun getGreatestNumberOfPenguins(graph: Graph, timeLimit: Int): Either<String, Either<NegativeWightCycle, ResultWalk>> =
    buildAdvancedGraph(graph).let { advancedGraph ->
        val startNode = advancedGraph.vertices.find {
            it == AdvancedVertex(SimpleVertex("M"), emptySet())
        }!!
        belmanFord(advancedGraph, startNode).fold(
            ifLeft = { it.left().right() },
            ifRight = { belmanFordResult ->
                val subsets = graph.vertices.filter(::isPenguin).toSet().subsets()
                subsets.toList().sortedByDescending { it.size }.find { subset ->
                    belmanFordResult.distances.getValue(doorVertexOf(subset, advancedGraph)) <= timeLimit
                }.toOption().map { maxSubset ->
                    ResultWalk(
                        belmanFordResult.pathFromStartTo(doorVertexOf(maxSubset, advancedGraph))
                            .mapNotNull { it as? AdvancedVertex },
                        graph.edges
                    ).right()
                }.toEither {
                    "There is no path that starts in M, ends in a door, and its length is lower than or equals to $timeLimit."
                }
            }
        )
    }

private fun doorVertexOf(subset: Set<Vertex>, advancedGraph: Graph) =
    advancedGraph.vertices.find {
        it == AdvancedVertex(SimpleVertex("D"), subset)
    }!!

private fun buildAdvancedGraph(original: Graph): Graph {
    val penguins = original.vertices.filter(::isPenguin)
    val subsets = penguins.toSet().subsets()
    val vertices =
        subsets.flatMap { subset ->
            original.vertices.map { originalVertex ->
                AdvancedVertex(originalVertex, subset)
            }
        }
    val verticesByComponents =
        vertices.map { (it.original to it.subset) to it }.toMap()
    val getVertex = { originalVertex: Vertex, subset: Set<Vertex> ->
        verticesByComponents.getValue(originalVertex to subset)
    }
    val edges =
        original.edges.flatMap { (i, j, weight) ->
            subsets.mapNotNull { subset ->
                if (isPenguin(i) && i !in subset) // 1
                    null
                else if (isPenguin(i)) // 2
                    if (isPenguin(j)) // 2.1
                        if (j in subset) // 2.1.a
                            Edge(getVertex(i, subset), getVertex(j, subset), weight)
                        else // 2.1.b
                            Edge(getVertex(i, subset), getVertex(j, (subset + j)), weight)
                    else // 2.2
                        Edge(getVertex(i, subset), getVertex(j, subset), weight)
                else // 3
                    if (isPenguin(j)) // 3.1
                        if (j in subset) // 3.1.a
                            Edge(getVertex(i, subset), getVertex(j, subset), weight)
                        else // 3.1.b
                            Edge(getVertex(i, subset), getVertex(j, subset + j), weight)
                    else // 3.2
                        Edge(getVertex(i, subset), getVertex(j, subset), weight)
            }
        }
    return Graph(vertices.toSet(), edges.toSet())
}

private fun isPenguin(vertex: Vertex) =
    vertex.identifier.startsWith("P")

class AdvancedVertex(
    val original: Vertex,
    val subset: Set<Vertex>
) : Vertex {
    override val identifier: String
        get() = original.identifier + "__" + subset.joinToString(",") { it.identifier }

    override fun equals(other: Any?) =
        if (other is AdvancedVertex)
            identifier == other.identifier
        else
            false

    override fun hashCode(): Int =
        (original to subset).hashCode()
}
