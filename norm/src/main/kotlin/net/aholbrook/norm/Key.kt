package net.aholbrook.norm

sealed interface Key {
    fun toList(): List<Any?>
}

class SingleKey<_KeyType>(val value: _KeyType? = null) : Key {
    override fun toList() = listOf(value)
}

class DuoKey<_KeyType1, _KeyType2> (
    val value1: _KeyType1? = null,
    val value2: _KeyType2? = null,
) : Key {
    override fun toList() = listOf(value1, value2)
}
