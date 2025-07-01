package com.github.xepozz.php_dump.stubs.any_tree

import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.CachingSimpleNode
import com.intellij.ui.treeStructure.SimpleNode

class LeafNode(
    val node: AnyNode,
    rootNode: SimpleNode
) : CachingSimpleNode(rootNode) {
    init {
//        presentation.setIcon(AllIcons.Nodes.NodePlaceholder)
        presentation.addText("\t", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        presentation.addText(node.name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        presentation.addText("\t", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        if (node.value.isNotEmpty()) {
            presentation.addText(node.value, SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

//    override fun buildChildren() = emptyArray<SimpleNode>()
    override fun buildChildren() = node.children.map { LeafNode(it, this) }.toTypedArray()

    override fun toString(): String {
        return "LeafNode(node=$node)"
    }
}