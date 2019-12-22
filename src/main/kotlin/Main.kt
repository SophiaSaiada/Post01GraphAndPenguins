import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun getBestPath(times: List<List<Int>>, timeLimit: Int) {

}

enum class GraphCreationError {
    MatrixIsNotASquare,
    MatrixIsTooSmall
}

fun buildGraph(matrix: List<List<Int>>): Either<GraphCreationError, Graph> {
    if (matrix.isEmpty())
        return Graph(emptySet(), emptySet()).right()
    if (matrix.size != matrix[0].size)
        return GraphCreationError.MatrixIsNotASquare.left()
    val numOfNodes = matrix.size
    if (numOfNodes < 2)
        return GraphCreationError.MatrixIsTooSmall.left()
    val vertices = ("M" + (1 until (numOfNodes - 1)).map { it.toString() } + "D").map {
        Vertex(it.toString())
    }
    val edges = matrix.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, time ->
            Edge(vertices[rowIndex], vertices[columnIndex], time)
        }
    }.flatten()
    return Graph(vertices.toSet(), edges.toSet()).right()
}