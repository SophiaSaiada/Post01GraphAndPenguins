import arrow.core.Either
import arrow.core.left
import arrow.core.right

enum class GraphCreationError(val description: String) {
    MatrixIsNotASquare("Matrix is not a square."),
    MatrixIsTooSmall("There should be at least 2 rows and columns.")
}

fun buildGraph(matrix: List<List<Int>>): Either<GraphCreationError, Graph> {
    if (matrix.isEmpty())
        return Graph(emptySet(), emptySet()).right()
    if (matrix.size != matrix[0].size)
        return GraphCreationError.MatrixIsNotASquare.left()
    val numOfNodes = matrix.size
    if (numOfNodes < 2)
        return GraphCreationError.MatrixIsTooSmall.left()
    val vertices =
        (listOf("M") + (1 until (numOfNodes - 1)).map { "P$it" } + "D")
            .map(::SimpleVertex)
    val edges =
        matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, time ->
                Edge(vertices[rowIndex], vertices[columnIndex], time)
            }
        }
    return Graph(vertices.toSet(), edges.toSet()).right()
}