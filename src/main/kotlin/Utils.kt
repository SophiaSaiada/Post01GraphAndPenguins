fun <K,V> Map<K,V?>.filterNotNullValues() =
    this.mapNotNull {
        it.value?.let { value -> it.key to value }
    }.toMap()