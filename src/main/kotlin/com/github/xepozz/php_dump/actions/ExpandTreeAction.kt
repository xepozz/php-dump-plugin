package com.github.xepozz.php_dump.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.JTree

class ExpandTreeAction(private val tree: JTree) :
    AnAction("Expand All", "Expand all", AllIcons.Actions.Expandall) {
    override fun actionPerformed(event: AnActionEvent) {
        TreeUtil.expandAll(tree)
    }
}