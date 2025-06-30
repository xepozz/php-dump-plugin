package com.github.xepozz.php_dump.tree

import com.github.xepozz.php_dump.stubs.token_object.TokensList
import com.intellij.ui.treeStructure.CachingSimpleNode
import com.intellij.ui.treeStructure.SimpleNode

class RootNode(val list: TokensList?) : CachingSimpleNode(null) {
    override fun getName() = "root node"

    override fun buildChildren(): Array<SimpleNode> {
        if (list == null) {
            return emptyArray()
        }
        return list.children.map { TokenNode(it, this) }.toTypedArray()
    }
}