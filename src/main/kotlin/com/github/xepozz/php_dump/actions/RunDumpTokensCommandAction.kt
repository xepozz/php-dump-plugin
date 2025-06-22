package com.github.xepozz.php_dump.actions

import com.github.xepozz.php_dump.services.TokensDumperService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager

class RunDumpTokensCommandAction() : AnAction("Dump Tokens in Terminal", null, AllIcons.Actions.Execute) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        println("project $project")
        val basePath = project.basePath ?: return
        println("basePath $basePath")
        val editorFactory = EditorFactory.getInstance()
        val editors = editorFactory.allEditors
        println("editors $editors")

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val file = editor.virtualFile ?: return
        println("file $file")

        val service = project.getService(TokensDumperService::class.java)

        service.dump(file.path, {
            println("dumping $basePath")
        })

    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}