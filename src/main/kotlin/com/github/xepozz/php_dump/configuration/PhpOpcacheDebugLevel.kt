package com.github.xepozz.php_dump.configuration

enum class PhpOpcacheDebugLevel(val value: String) {
    BEFORE_OPTIMIZATION("0x10000"),
    AFTER_OPTIMIZATION("0x20000"),
    CONTEXT_FREE("0x40000"),
    SSA_FORM("0x200000"),
}