package com.github.xepozz.php_dump.panel

import com.github.xepozz.php_dump.PhpDumpIcons
import com.github.xepozz.php_dump.actions.RefreshAction
import com.github.xepozz.php_dump.configuration.PhpDumpSettingsService
import com.github.xepozz.php_dump.configuration.PhpOpcacheDebugLevel
import com.github.xepozz.php_dump.services.OpcodesDumperService
import com.github.xepozz.php_opcodes_language.language.PHPOpFileType
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
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
    private val document = EditorFactory.getInstance().createDocument("")
    private val editor = EditorFactory.getInstance().createViewer(document, project, EditorKind.MAIN_EDITOR) as EditorEx

    init {
        viewComponent = editor.component
        configureEditor()

        createToolBar()
        createContent()
    }

    private fun configureEditor() {
        editor.settings.apply {
            isBlinkCaret = true
            isCaretRowShown = true
            isBlockCursor = false
            isLineMarkerAreaShown = true
            isHighlightSelectionOccurrences = true
        }

        editor.setCaretEnabled(true)

        ApplicationManager.getApplication().runReadAction {
            val highlighter = EditorHighlighterFactory.getInstance()
                .createEditorHighlighter(project, PHPOpFileType.INSTANCE)

            editor.highlighter = highlighter
        }
    }

    private fun createToolBar() {
        val actionGroup = DefaultActionGroup().apply {
            add(RefreshAction { refresh(project, RefreshType.MANUAL) })
            add(object : AnAction("Clear Output", "Clear the output", AllIcons.Actions.GC) {
                override fun actionPerformed(e: AnActionEvent) {
                    WriteCommandAction.runWriteCommandAction(project) {
                        document.setText("")
                    }
                }
            })
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

        val content = result as? String ?: "No output"

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText(content)
        }
    }

    override fun dispose() {
        EditorFactory.getInstance().releaseEditor(editor)
    }
}

