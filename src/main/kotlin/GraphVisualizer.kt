import arrow.core.extensions.set.foldable.exists

object GraphVisualizer {
    fun getDotFileOf(graph: Graph): String {
        val content =
            if (isAdvancedGraph(graph)) getAdvancedDotRepresentationOf(graph)
            else getDotRepresentationOf(graph)
        getDotRepresentationOf(graph)
        return """
            |digraph G {
            |    fontname = "Lora Bold Italic";
            |    node [fontname = "Lora Bold Italic"];
            |    rankdir = LR;
            |
            |${content.indent()}
            |}
        """.trimMargin()
    }

    private fun getDotRepresentationOf(graph: Graph): String {
        val vertices =
            graph.vertices.filter { it.identifier.startsWith("M") }.joinToString("\n") {
                dotRepresentationOf(it, GraphVisualizer.Shape.Square)
            } + """|
                |{
                |   rank = same;
                |
            """.trimMargin() +
                    graph.vertices.filter { it.identifier.startsWith("P") }.sortedByDescending {
                        it.identifier.removePrefix("P").toIntOrNull() ?: -1
                    }.joinToString("\n") {
                        dotRepresentationOf(it, GraphVisualizer.Shape.Circle)
                    }.indent() + """|
                        |}
                        |
                    """.trimMargin() +
                    graph.vertices.filter { it.identifier.startsWith("D") }.joinToString("\n") {
                        dotRepresentationOf(it, GraphVisualizer.Shape.Square)
                    }
        val edges = graph.edges.joinToString("\n") { dotRepresentationOf(it, true) }
        return """
            |ranksep = 1.5;
            |nodesep = 0.5;
            |$vertices
            |
            |$edges
        """.trimMargin()
    }

    private fun getAdvancedDotRepresentationOf(graph: Graph): String {
        val advancedVertices = graph.vertices.mapNotNull { it as? AdvancedVertex }
        val subsets = advancedVertices.map { it.subset }.toSet()
        val vertices =
            subsets.joinToString("\n") { subset ->
                val verticesInLayer = advancedVertices.filter { it.subset == subset }
                val subsetsVertices = verticesInLayer.filter { it.identifier.startsWith("M") }.joinToString("\n") {
                    dotRepresentationOf(
                        it,
                        GraphVisualizer.Shape.Box,
                        if (it.subset.isEmpty()) Color("#C2185B") else null
                    )
                } + """|
                |{
                |   rank = same;
                |
            """.trimMargin() +
                        verticesInLayer.filter { it.identifier.startsWith("P") }.sortedByDescending {
                            it.identifier.removePrefix("P").toIntOrNull() ?: -1
                        }.joinToString("\n") {
                            dotRepresentationOf(it, GraphVisualizer.Shape.Ellipse)
                        }.indent() + """|
                        |}
                        |
                    """.trimMargin() +
                        verticesInLayer.filter { it.identifier.startsWith("D") }.joinToString("\n") {
                            dotRepresentationOf(it, GraphVisualizer.Shape.Box, Color("#00ACC1"))
                        }
                """subgraph cluster___${subset.joinToString("__") { nodeIdentifierOf(it) }} {
                    |    color = lightgrey;
                    |    label = <Layer {${subset.toList()
                    .sortedBy {
                        it.identifier.removePrefix("P").toIntOrNull() ?: -1
                    }.joinToString(", ") { it.identifier }}}>
                    |
                    |${subsetsVertices.indent()}
                    |}
                """.trimMargin()
            }
        val edges = graph.edges.joinToString("\n") { dotRepresentationOf(it) }
        return vertices + "\n\n" + edges
    }

    private fun nodeIdentifierOf(vertex: Vertex): String =
        if (vertex is AdvancedVertex)
            nodeIdentifierOf(vertex)
        else
            vertex.identifier

    private fun nodeIdentifierOf(vertex: AdvancedVertex) =
        nodeIdentifierOf(vertex.original) + "___" + vertex.subset.joinToString("__") { nodeIdentifierOf(it) }

    private fun labelOf(vertex: Vertex) =
        "\"" + vertex.identifier + "\""

    private fun labelOf(vertex: AdvancedVertex) =
        "<" + vertex.original.identifier +
                "<SUB>{" +
                vertex.subset.toList().sortedBy {
                    it.identifier.removePrefix("P").toIntOrNull() ?: -1
                }.joinToString(", ") { it.identifier } +
                "}</SUB>" + ">"


    private fun dotRepresentationOf(
        vertex: Vertex,
        shape: Shape = GraphVisualizer.Shape.Ellipse,
        color: Color? = null
    ) =
        nodeIdentifierOf(vertex) + "[label=${labelOf(vertex)}, shape=$shape${color?.let { ", color=\"${it.hex}\", fontcolor=\"${it.hex}\"" }}];"

    private fun dotRepresentationOf(
        vertex: AdvancedVertex,
        shape: Shape = GraphVisualizer.Shape.Ellipse,
        color: Color? = null
    ) =
        nodeIdentifierOf(vertex) + "[label=${labelOf(vertex)}, shape=$shape${color?.let { ", color=\"${it.hex}\", fontcolor=\"${it.hex}\"" }}];"

    private fun dotRepresentationOf(edge: Edge, xLabel: Boolean = false) =
        nodeIdentifierOf(edge.start) + " -> " + nodeIdentifierOf(edge.end) + " [dir=${if (edge.start < edge.end) "forward" else "backward"}, ${if (xLabel) "x" else ""}label=\"${edge.weight}\"];"

    private fun isAdvancedGraph(graph: Graph) =
        graph.vertices.exists { it is AdvancedVertex }

    private fun String.indent() =
        this.lines().joinToString("\n") {
            "    $it"
        }

    private operator fun Vertex.compareTo(other: Vertex) =
        identifier.compareTo(other.identifier)

    private enum class Shape {
        Square,
        Circle,
        Box,
        Ellipse;

        override fun toString() =
            this.name.toLowerCase()
    }

    private data class Color(val hex: String)
}