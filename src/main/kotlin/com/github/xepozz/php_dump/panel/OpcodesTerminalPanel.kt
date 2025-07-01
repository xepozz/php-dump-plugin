package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.PhpDumpIcons
import com.github.xepozz.php_dump.actions.ClearConsoleViewAction
import com.github.xepozz.php_dump.actions.RefreshAction
import com.github.xepozz.php_dump.configuration.PhpDumpSettingsService
import com.github.xepozz.php_dump.configuration.PhpOpcacheDebugLevel
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
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
) : SimpleToolWindowPanel(false, false), RefreshablePanel, Disposable {
    val viewComponent: JComponent
    val service: OpcodesDumperService = project.getService(OpcodesDumperService::class.java)
    val state = PhpDumpSettingsService.getInstance(project)
    val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

    init {
        viewComponent = consoleView.component

        createToolBar()
        createContent()
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup().apply {
            add(RefreshAction { refresh(project, RefreshType.MANUAL) })
            add(ClearConsoleViewAction(consoleView))
            add(object : AnAction(
                "Enable Auto Refresh", "Turns on or off auto refresh of panel context",
                if (state.autoRefresh) PhpDumpIcons.RESTART_STOP else PhpDumpIcons.RERUN_AUTOMATICALLY
            ) {
                override fun actionPerformed(e: AnActionEvent) {
                    state.autoRefresh = !state.autoRefresh
                }

                override fun update(e: AnActionEvent) {
                    if (state.autoRefresh) {
                        e.presentation.text = "Disable Auto Refresh"
                        e.presentation.icon = PhpDumpIcons.RESTART_STOP
                    } else {
                        e.presentation.text = "Enable Auto Refresh"
                        e.presentation.icon = PhpDumpIcons.RERUN_AUTOMATICALLY
                    }
                }
            })
            addSeparator()
            add(DefaultActionGroup("Debug Level", true).apply {
                mapOf(
                    PhpOpcacheDebugLevel.BEFORE_OPTIMIZATION to "Before Optimization (0x10000)",
                    PhpOpcacheDebugLevel.AFTER_OPTIMIZATION to "After Optimization (0x20000)",
                    PhpOpcacheDebugLevel.CONTEXT_FREE to "Context Free (0x40000)",
                    PhpOpcacheDebugLevel.SSA_FORM to "Static Single Assignment Form (0x200000)",
                )
                    .map { (level, label) ->
                        object : AnAction(label) {
                            override fun actionPerformed(e: AnActionEvent) {
                                state.debugLevel = level
                                refresh(project, RefreshType.MANUAL)
                            }

                            override fun update(e: AnActionEvent) {
                                e.presentation.icon = when (state.debugLevel) {
                                    level -> AllIcons.Actions.Checked
                                    else -> null
                                }
                            }
                        }
                    }
                    .also { addAll(it) }

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

        val result = runBlocking { service.dump(virtualFile) }

        consoleView.clear()
        consoleView.print(result as? String ?: "No output", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun dispose() {
    }
}

