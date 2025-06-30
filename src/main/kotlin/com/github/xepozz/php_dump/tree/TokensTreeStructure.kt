package com.github.xepozz.php_dump.tree

import com.intellij.ui.treeStructure.SimpleTreeStructure

class TokensTreeStructure(val root: RootNode) : SimpleTreeStructure() {
    override fun getRootElement() = root
}