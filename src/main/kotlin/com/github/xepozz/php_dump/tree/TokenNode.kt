package com.github.xepozz.php_dump.tree

import com.github.xepozz.php_dump.stubs.token_object.Token
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.CachingSimpleNode
import com.intellij.ui.treeStructure.SimpleNode

class TokenNode(
    val node: Token,
    rootNode: RootNode
) : CachingSimpleNode(rootNode) {
    init {
//        presentation.setIcon(AllIcons.Nodes.NodePlaceholder)
        presentation.addText("${node.pos}-${node.endPos}:", SimpleTextAttributes.DARK_TEXT)
        presentation.addText("\t", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        presentation.addText(""""${node.value}"""", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        presentation.addText("\t", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        presentation.addText("(${node.name})", SimpleTextAttributes.GRAYED_ATTRIBUTES)
    }

    override fun buildChildren() = emptyArray<SimpleNode>()

    override fun toString(): String {
        return "TokenNode(node=$node)"
    }
}