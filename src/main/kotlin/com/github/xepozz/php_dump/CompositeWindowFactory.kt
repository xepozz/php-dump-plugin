package com.github.xepozz.php_dump

import com.github.xepozz.php_dump.panel.OpcodesTerminalPanel
import com.github.xepozz.php_dump.panel.TokensTerminalPanel
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.github.xepozz.php_dump.services.TokensDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.Icon

open class CompositeWindowFactory : ToolWindowFactory, DumbAware {
    override val icon: Icon?
        get() = PhpDumpIcons.POT

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val contentManager = toolWindow.contentManager

        val opcodesTerminalLayout = run {
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

            val service = toolWindow.project.getService(OpcodesDumperService::class.java)
            service.consoleView = consoleView

            OpcodesTerminalPanel(consoleView.component)
        }

        val tokensTerminalLayout = run {
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

            val service = toolWindow.project.getService(TokensDumperService::class.java)
            service.consoleView = consoleView

            TokensTerminalPanel(consoleView.component)
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
            this.createContent(tokensTerminalLayout, "Tokens", false).apply {
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