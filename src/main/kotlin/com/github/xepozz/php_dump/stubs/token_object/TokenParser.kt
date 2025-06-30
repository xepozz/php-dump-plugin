package com.github.xepozz.php_dump.stubs.token_object

import kotlinx.serialization.json.Json

object TokenParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseTokens(jsonString: String) = TokensList(json.decodeFromString<List<Token>>(jsonString))

    fun serializeTokens(tokens: List<Token>): String = json.encodeToString(TokensList.serializer(), TokensList(tokens))
}