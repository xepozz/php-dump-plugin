package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.actions.RunDumpTokensCommandAction
import com.github.xepozz.php_dump.services.DebugLevelState
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.JBColor
import com.intellij.util.IconUtil
import kotlinx.coroutines.runBlocking
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
    val state = DebugLevelState.getInstance(project)

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
        actionGroup.add(RunDumpTokensCommandAction(service))
        actionGroup.addSeparator()
        actionGroup.add(DefaultActionGroup("Debug Level", true).apply {
            add(object : AnAction("Before Optimization") {
                override fun actionPerformed(e: AnActionEvent) {
                    state.setDebugLevel(1)
                    refresh(project)
                }
                override fun update(e: AnActionEvent) {
                    e.presentation.icon = when (state.getDebugLevel()) {
                        1 -> IconUtil.colorize(AllIcons.General.GreenCheckmark, JBColor.GRAY)
                        else -> null
                    }
                }
            })
            add(object : AnAction("After Optimization") {
                override fun actionPerformed(e: AnActionEvent) {
                    state.setDebugLevel(2)
                    refresh(project)
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.icon = when (state.getDebugLevel()) {
                        2 -> IconUtil.colorize(AllIcons.General.GreenCheckmark, JBColor.GRAY)
                        else -> null
                    }
                }
            })
            templatePresentation.icon = AllIcons.Actions.ToggleVisibility
        })

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

        runBlocking { service.dump(virtualFile) }
    }
}
