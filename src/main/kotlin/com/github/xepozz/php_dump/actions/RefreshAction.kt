package com.github.xepozz.php_dump.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RefreshAction(private val callback: () -> Unit) : AnAction("Refresh", "Refresh", AllIcons.Actions.Refresh) {
    override fun actionPerformed(event: AnActionEvent) {
        callback()
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}