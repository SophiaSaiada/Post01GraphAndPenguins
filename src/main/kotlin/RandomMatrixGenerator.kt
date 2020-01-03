import kotlin.random.Random

fun generateRandomMatrix(numOfPenguins: Int, allowedWeights: List<Int>) =
    List(numOfPenguins + 2) { i ->
        List(numOfPenguins + 2) { j ->
            if (i == j) 0
            else
                allowedWeights[Random.nextInt(allowedWeights.size)].takeIf { it != -1 }
                    ?: allowedWeights[Random.nextInt(allowedWeights.size)]
        }
    }.also { matrix ->
        println(
            "listOf(\n" +
                    matrix.joinToString(",\n") {
                        "listOf(" + it.joinToString() + ")"
                    } +
                    "\n)"
        )
        println(matrix.joinToString(" \\\\\n")
        { it.joinToString(" & ") })
    }
