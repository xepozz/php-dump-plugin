package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.actions.RunDumpTokensCommandAction
import com.github.xepozz.php_dump.services.TokensObjectDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JPanel

class TokensObjectTerminalPanel(
    val project: Project,
) : SimpleToolWindowPanel(false, false), RefreshablePanel {
    var viewComponent: JComponent
    var service: TokensObjectDumperService

    init {
        val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        viewComponent = consoleView.component

        service = project.getService(TokensObjectDumperService::class.java)
        service.consoleView = consoleView

        createToolBar()
        createContent()
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(RunDumpTokensCommandAction(service))
        actionGroup.addSeparator()
//        actionGroup.add(OpenSettingsAction())

        val actionToolbar = ActionManager.getInstance().createActionToolbar("Opcodes Toolbar", actionGroup, false)
        actionToolbar.targetComponent = this

        val toolBarPanel = JPanel(GridLayout())
        toolBarPanel.add(actionToolbar.component)

        toolbar = toolBarPanel
    }

    private fun createContent() {
        val responsivePanel = JPanel(BorderLayout())
        responsivePanel.add(viewComponent, BorderLayout.CENTER)
        responsivePanel.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                viewComponent.revalidate()
                viewComponent.repaint()
            }
        })

        setContent(responsivePanel)
    }

    override fun refresh(project: Project, type: RefreshType) {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = editor.virtualFile ?: return

        runBlocking { service.dump(virtualFile) }
    }
}