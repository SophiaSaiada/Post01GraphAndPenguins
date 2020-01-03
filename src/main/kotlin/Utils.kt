fun <K, V> Map<K, V?>.filterNotNullValues() =
    this.mapNotNull {
        it.value?.let { value -> it.key to value }
    }.toMap()

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> =
    mapIndexed(transform).flatten()

fun <T> Set<T>.subsets(ofSize: Int = this.size): Set<Set<T>> {
    if (ofSize == 0)
        return setOf(setOf())
    return subsets(ofSize - 1).flatMap { smallerSubset ->
        if (smallerSubset.size == ofSize - 1)
            (this.map { smallerSubset + it } + setOf(smallerSubset)).toSet()
        else
            setOf(smallerSubset)
    }.toSet()
}