data class Vertex(val identifier: String)

class Edge(val start: Vertex, val end: Vertex, val weight: Int)

class Graph(
    val vertices: Set<Vertex>,
    val edges: Set<Edge>
)