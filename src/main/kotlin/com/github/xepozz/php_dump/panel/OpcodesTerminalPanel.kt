package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.actions.RunDumpCommandAction
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JPanel

class OpcodesTerminalPanel(
    val project: Project,
) : SimpleToolWindowPanel(false, false), RefreshablePanel {
    val viewComponent: JComponent
    val service: OpcodesDumperService

    init {
        val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        viewComponent = consoleView.component

        service = project.getService(OpcodesDumperService::class.java)
        service.consoleView = consoleView

        createToolBar()
        createContent()
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(RunDumpCommandAction())
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

    override fun refresh(project: Project) {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = editor.virtualFile ?: return

        service.dump(virtualFile.path)
    }
}