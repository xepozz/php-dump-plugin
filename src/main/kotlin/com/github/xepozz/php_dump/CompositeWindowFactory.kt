package com.github.xepozz.php_dump

import com.github.xepozz.php_dump.panel.OpcodesTerminalPanel
import com.github.xepozz.php_dump.panel.TokenTreePanel
import com.github.xepozz.php_dump.panel.TokensObjectTerminalPanel
import com.github.xepozz.php_dump.panel.TokensObjectTreeTerminalPanel
import com.github.xepozz.php_dump.panel.TokensTerminalPanel
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

open class CompositeWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val contentManager = toolWindow.contentManager

        val opcodesTerminalLayout = OpcodesTerminalPanel(project)
        val tokensTerminalLayout = TokensTerminalPanel(project)
        val tokensObjectTerminalLayout = TokensObjectTerminalPanel(project)
        val tokensObjectTreeTerminalLayout = TokensObjectTreeTerminalPanel(project)

        val treeWindow = TokenTreePanel(project)

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
            this.createContent(tokensObjectTerminalLayout, "Tokens Object", false).apply {
                contentManager.addContent(
                    this.apply {
                        this.isPinnable = true
                        this.isCloseable = false
                    }
                )
            }
            this.createContent(tokensObjectTreeTerminalLayout, "Tokens Object Tree", false).apply {
                contentManager.addContent(
                    this.apply {
                        this.isPinnable = true
                        this.isCloseable = false
                    }
                )
            }
            this.createContent(treeWindow.component, "Tree panel", false).apply {
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