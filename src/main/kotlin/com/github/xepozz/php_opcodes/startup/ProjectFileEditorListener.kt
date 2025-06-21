package com.github.xepozz.php_opcodes.startup

import com.github.xepozz.php_opcodes.services.OpcodesDumperService
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project

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

        val virtualFile = event.newFile ?: return

        val service = project.getService(OpcodesDumperService::class.java)

        service.dump(virtualFile.path) {
            println("dump in selection changed ")
        }
    }
}