package com.github.xepozz.php_dump.startup

import com.github.xepozz.php_dump.panel.RefreshType
import com.github.xepozz.php_dump.panel.RefreshablePanel
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

class ProjectFileEditorListener(val project: Project) : FileEditorManagerListener {
//    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
//        super.fileClosed(source, file)
//        println("file closed $source, $file")
//    }
//    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
//        super.fileOpened(source, file)
//        println("file opened $source, $file")
//    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        println("selection changed $event")

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("PHP Dump")

        toolWindow
            ?.component
            ?.components
            ?.mapNotNull { it as? RefreshablePanel }
            ?.forEach { it.refresh(project, RefreshType.AUTO) }
            ?: return

    }
}