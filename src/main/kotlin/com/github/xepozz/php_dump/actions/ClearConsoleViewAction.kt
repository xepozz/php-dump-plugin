package com.github.xepozz.php_dump.actions

import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ClearConsoleViewAction(val consoleView: ConsoleView) : AnAction("Clear", "Clear console", AllIcons.Actions.GC) {
    override fun actionPerformed(e: AnActionEvent) {
        consoleView.clear()
    }
}