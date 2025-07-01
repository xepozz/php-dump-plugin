package com.github.xepozz.php_dump.stubs.any_tree

import com.intellij.ui.treeStructure.SimpleTreeStructure

class AnyTreeStructure(val root: AnyRootNode) : SimpleTreeStructure() {
    override fun getRootElement() = root
}