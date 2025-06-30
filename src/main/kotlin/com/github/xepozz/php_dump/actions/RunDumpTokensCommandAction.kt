package com.github.xepozz.php_dump.actions

import com.github.xepozz.php_dump.services.DumperServiceInterface
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import kotlinx.coroutines.runBlocking

class RunDumpTokensCommandAction(
    val dumpService: DumperServiceInterface
) : AnAction("Dump Tokens in Terminal", null, AllIcons.Actions.Execute) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        println("project $project")
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val file = editor.virtualFile ?: return
        println("file $file")

        runBlocking { dumpService.dump(file) }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}