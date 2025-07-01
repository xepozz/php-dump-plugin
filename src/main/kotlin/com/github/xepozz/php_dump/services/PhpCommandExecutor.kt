package com.github.xepozz.php_dump.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.project.Project
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import kotlin.coroutines.suspendCoroutine

object PhpCommandExecutor {
    suspend fun execute(
        file: String,
        phpSnippet: String,
        project: Project,
        processListener: ProcessAdapter,
        processArguments: List<String> = emptyList()
    ) {
        val interpretersManager = PhpInterpretersManagerImpl.getInstance(project)
        val interpreter = PhpProjectConfigurationFacade.getInstance(project).interpreter
            ?: interpretersManager.interpreters.firstOrNull() ?: return

        val interpreterPath = interpreter.pathToPhpExecutable ?: return
        val commandArgs = buildList {
            add(interpreterPath)
            addAll(processArguments)
            add("-r")
            add(phpSnippet)

            add(file)
        }

        executeCommand(commandArgs, processListener)
    }

    private suspend fun executeCommand(commandArgs: List<String>, processListener: ProcessAdapter) =
        suspendCoroutine<Int> { continuation ->
            val command = GeneralCommandLine(commandArgs)
            command.withRedirectErrorStream(false)

            println("running command ${command.commandLineString}")
            val processHandler = KillableColoredProcessHandler.Silent(command)
            processHandler.setShouldKillProcessSoftly(false)
            processHandler.setShouldDestroyProcessRecursively(true)
            processHandler.addProcessListener(processListener)
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun processTerminated(event: ProcessEvent) {
                    continuation.resumeWith(Result.success(event.exitCode))
                }

                override fun processNotStarted() {
                    continuation.resumeWith(Result.failure(Error("process was not started")))
                }
            })

            processHandler.startNotify()
        }
}