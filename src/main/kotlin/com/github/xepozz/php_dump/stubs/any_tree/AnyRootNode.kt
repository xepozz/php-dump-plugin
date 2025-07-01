package com.github.xepozz.php_dump.stubs.any_tree

import com.intellij.ui.treeStructure.CachingSimpleNode
import com.intellij.ui.treeStructure.SimpleNode

class AnyRootNode(val list: AnyNodeList?) : CachingSimpleNode(null) {
    override fun getName() = "root node"

    override fun buildChildren(): Array<SimpleNode> {
        if (list == null) {
            return emptyArray()
        }
        return list.children.map { LeafNode(it, this) }.toTypedArray()
    }
}