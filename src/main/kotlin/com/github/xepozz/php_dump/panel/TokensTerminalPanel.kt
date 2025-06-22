package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.actions.RunDumpTokensCommandAction
import com.github.xepozz.php_dump.services.TokensDumperService
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

class TokensTerminalPanel(
    val terminalViewComponent: JComponent,
) : SimpleToolWindowPanel(false, false), RefreshablePanel {
    init {
        createToolBar()
        createContent()
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(RunDumpTokensCommandAction())
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
        responsivePanel.add(terminalViewComponent, BorderLayout.CENTER)
        responsivePanel.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                terminalViewComponent.revalidate()
                terminalViewComponent.repaint()
            }
        })

        setContent(responsivePanel)
    }

    override fun refresh(project: Project) {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = editor.virtualFile ?: return

        TokensDumperService.dump(virtualFile, project)
    }
}