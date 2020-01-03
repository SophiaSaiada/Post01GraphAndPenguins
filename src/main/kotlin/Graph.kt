interface Vertex {
    val identifier: String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

data class SimpleVertex(override val identifier: String) : Vertex

data class Edge(val start: Vertex, val end: Vertex, val weight: Int)

class Graph(
    val vertices: Set<Vertex>,
    val edges: Set<Edge>
)