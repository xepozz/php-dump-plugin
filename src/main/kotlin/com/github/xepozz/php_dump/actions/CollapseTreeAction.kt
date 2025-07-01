package com.github.xepozz.php_dump.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.JTree

class CollapseTreeAction(private val tree: JTree) :
    AnAction("Collapse All", "Collapse all", AllIcons.Actions.Collapseall) {
    override fun actionPerformed(event: AnActionEvent) {
        TreeUtil.collapseAll(tree, 2)
    }
}