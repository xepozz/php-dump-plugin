package com.github.xepozz.php_dump.stubs.any_tree

import kotlinx.serialization.json.Json

object AnyNodeParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseAnyNode(jsonString: String) = AnyNodeList(json.decodeFromString<List<AnyNode>>(jsonString))

    fun serializeAnyNodes(tokens: List<AnyNode>): String = json.encodeToString(AnyNodeList.serializer(), AnyNodeList(tokens))
}