package com.github.xepozz.php_dump

import com.github.xepozz.php_dump.panel.OpcodesTerminalPanel
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.filters.UrlFilter
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import javax.swing.Icon

open class CompositeWindowFactory : ToolWindowFactory, DumbAware {
    override val icon: Icon?
        get() = PhpDumpIcons.POT

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val contentManager = toolWindow.contentManager

        val opcodesTerminalLayout = run {
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

            val opcodesDumperService = toolWindow.project.getService(OpcodesDumperService::class.java)
            opcodesDumperService.consoleView = consoleView

        val terminalLayout = OpcodesTerminalPanel(consoleView.component)
            OpcodesTerminalPanel(consoleView.component)
        }

        contentFactory.apply {
            this.createContent(opcodesTerminalLayout, "Opcodes", false).apply {
                contentManager.addContent(
                    this.apply {
                        this.isPinnable = true
                        this.isCloseable = false
                    }
                )
            }
        }
    }
}