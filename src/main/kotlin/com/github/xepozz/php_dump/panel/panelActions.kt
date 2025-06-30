package com.github.xepozz.php_dump.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.util.ui.tree.TreeUtil
import javax.swing.JComponent
import javax.swing.JTree

fun createCollapseAll(tree: JTree): JComponent {
    val presentation = Presentation()
    presentation.text = "Collapse All"
    presentation.icon = AllIcons.Actions.Collapseall

    return ActionButton(object : AnAction() {
        override fun actionPerformed(event: AnActionEvent) {
            TreeUtil.collapseAll(tree, 2)
        }

    }, presentation, "unknown", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE)
}

fun createExpandsAll(tree: JTree): JComponent {
    val presentation = Presentation()
    presentation.text = "Expand All"
    presentation.icon = AllIcons.Actions.Expandall

    return ActionButton(object : AnAction() {
        override fun actionPerformed(event: AnActionEvent) {
            TreeUtil.expandAll(tree)
        }

    }, presentation, "unknown", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE)
}

fun createRefreshButton(callback: ()->Unit): JComponent {
    val presentation = Presentation()
    presentation.text = "Refresh"
    presentation.icon = AllIcons.Actions.Refresh

    return ActionButton(object : AnAction() {
        override fun actionPerformed(event: AnActionEvent) {
            callback()
        }

    }, presentation, "unknown", ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE)
}
