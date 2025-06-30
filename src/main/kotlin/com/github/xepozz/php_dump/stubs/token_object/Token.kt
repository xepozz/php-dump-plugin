package com.github.xepozz.php_dump.stubs.token_object

import kotlinx.serialization.Serializable

@Serializable
data class TokensList(
    val children: List<Token> = listOf(),
)

@Serializable
data class Token(
    val line: Int,
    val pos: Int,
    val name: String,
    val value: String
) {
    val endPos: Int get() = pos + value.length

    override fun toString(): String {
        return "Token(line=$line, pos=$pos, name='$name', value='$value')"
    }
}
