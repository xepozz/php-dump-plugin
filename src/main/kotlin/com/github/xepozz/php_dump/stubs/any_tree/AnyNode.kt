package com.github.xepozz.php_dump.stubs.any_tree

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class AnyNodeList(
    val children: List<AnyNode> = listOf(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AnyNode(
    val name: String,
    val value: String = "",
//    @kotlinx.serialization.Transient
    val children: Collection<AnyNode> = emptyList()
) {
    override fun toString(): String {
        return "AnyNode(name='$name', value='$value', children=$children)"
    }
}
