package com.github.xepozz.php_dump

import com.github.xepozz.php_dump.panel.OpcacheSettingsPanel
import com.github.xepozz.php_dump.panel.OpcodesTerminalPanel
import com.github.xepozz.php_dump.panel.TokenTreePanel
import com.github.xepozz.php_dump.panel.TokensObjectTerminalPanel
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
        val opcodesSettingsLayout = OpcacheSettingsPanel(project)
        val tokensTerminalLayout = TokensTerminalPanel(project)
        val tokensObjectTerminalLayout = TokensObjectTerminalPanel(project)
        val tokenTreeLayout = TokenTreePanel(project)

        contentFactory.apply {
            this.createContent(opcodesTerminalLayout, "OpCodes", false).apply {
                contentManager.addContent(
                    this.apply {
                        this.isPinnable = true
                        this.isCloseable = false
                    }
                )
            }
            this.createContent(opcodesSettingsLayout, "OpCache Settings", false).apply {
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
            this.createContent(tokenTreeLayout.component, "Tree panel", false).apply {
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