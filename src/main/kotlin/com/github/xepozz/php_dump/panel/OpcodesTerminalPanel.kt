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
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.jetbrains.php.lang.PhpFileType
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
    val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

    init {
        viewComponent = consoleView.component

        service = project.getService(OpcodesDumperService::class.java)
        service.consoleView = consoleView

        createToolBar()
        createContent()
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup().apply {
            add(RunDumpTokensCommandAction(service, "Dump Opcodes"))
            add(object : AnAction("Clear", "Clear console", AllIcons.Actions.GC) {
                override fun actionPerformed(e: AnActionEvent) {
                    consoleView.clear()
                }
            })
            add(object : AnAction(
                "Enable Auto Refresh", "Turns on or off auto refresh of panel context",
                if (state.autoRefresh) AllIcons.Actions.RestartStop else AllIcons.Actions.RerunAutomatically
            ) {
                override fun actionPerformed(e: AnActionEvent) {
                    state.autoRefresh = !state.autoRefresh
                }

                override fun update(e: AnActionEvent) {
                    if (state.autoRefresh) {
                        e.presentation.text = "Disable Auto Refresh"
                        e.presentation.icon = AllIcons.Actions.RestartStop
                    } else {
                        e.presentation.text = "Enable Auto Refresh"
                        e.presentation.icon = AllIcons.Actions.RerunAutomatically
                    }
                }
            })
            addSeparator()
            add(DefaultActionGroup("Debug Level", true).apply {
                add(object : AnAction("Before Optimization") {
                    override fun actionPerformed(e: AnActionEvent) {
                        state.debugLevel = 1
                        refresh(project, RefreshType.MANUAL)
                    }

                    override fun update(e: AnActionEvent) {
                        e.presentation.icon = when (state.debugLevel) {
                            1 -> AllIcons.Actions.Checked
                            else -> null
                        }
                    }
                })
                add(object : AnAction("After Optimization") {
                    override fun actionPerformed(e: AnActionEvent) {
                        state.debugLevel = 2
                        refresh(project, RefreshType.MANUAL)
                    }

                    override fun update(e: AnActionEvent) {
                        e.presentation.icon = when (state.debugLevel) {
                            2 -> AllIcons.Actions.Checked
                            else -> null
                        }
                    }
                })
                templatePresentation.icon = AllIcons.Actions.ToggleVisibility
            })

            add(object : AnAction("Select Preload File", "Choose a file", AllIcons.Actions.MenuOpen) {
                override fun actionPerformed(e: AnActionEvent) {

                    val fileChooserDescriptor =
                        FileChooserDescriptorFactory.createSingleFileDescriptor(PhpFileType.INSTANCE)
                            .withTitle("Select Preload File")
                            .withDescription("Choose a preload.php file")

                    FileChooser
                        .chooseFile(fileChooserDescriptor, project, null)
                        .let { file ->
                            state.preloadFile = file?.path
                        }
                    refresh(project, RefreshType.MANUAL)
                }
            })
        }

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
        if (type == RefreshType.AUTO && !state.autoRefresh) {
            return
        }
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = editor.virtualFile ?: return

        runBlocking { service.dump(virtualFile) }
    }
}
