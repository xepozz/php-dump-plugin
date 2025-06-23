package com.github.xepozz.php_dump.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.openapi.project.Project
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PhpCommandExecutor {
    fun execute(file: String, phpSnippet: String, project: Project, processListener: ProcessAdapter) {
        val interpretersManager = PhpInterpretersManagerImpl.getInstance(project)
        val interpreter = PhpProjectConfigurationFacade.getInstance(project).interpreter
            ?: interpretersManager.interpreters.firstOrNull() ?: return

        val commandArgs = buildList {
            interpreter.apply {
                println("interpreter: $this")
                add(this.pathToPhpExecutable!!)
            }
            add("-r")
            add(phpSnippet)

            add(file)
        }

        CoroutineScope(Dispatchers.IO).launch {
            executeCommand(commandArgs, processListener)
        }
    }

    private suspend fun executeCommand(commandArgs: List<String>, processListener: ProcessAdapter) =
        withContext(Dispatchers.IO) {
            val command = GeneralCommandLine(commandArgs)
            command.withRedirectErrorStream(false)

            println("running command ${command.commandLineString}")
            val processHandler = KillableColoredProcessHandler.Silent(command)
            processHandler.setShouldKillProcessSoftly(false)
            processHandler.setShouldDestroyProcessRecursively(true)
            processHandler.addProcessListener(processListener)

            processHandler.startNotify()
        }
}