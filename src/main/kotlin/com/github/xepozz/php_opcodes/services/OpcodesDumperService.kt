package com.github.xepozz.php_opcodes.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.ui.jcef.JBCefBrowser
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class OpcodesDumperService(var project: Project) : Disposable {
    var browser: JBCefBrowser? = null
    var consoleView: ConsoleView? = null

    override fun dispose() {
        consoleView?.dispose()
        browser?.dispose()
    }

    fun dump(file: String, callback: () -> Unit) {
        val interpretersManager = PhpInterpretersManagerImpl.getInstance(project)
        val interpreter = PhpProjectConfigurationFacade.getInstance(project).interpreter
            ?: interpretersManager.interpreters.firstOrNull() ?: return

//php -l \
// -ddisplay_errors=0 \
// -derror_reporting=0 \
// -dopcache.enable_cli=1 \
// -dopcache.save_comments=1 \
// -dopcache.opt_debug_level=0x10000 \
// -dopcache.optimization_level=0 \
// playground/test.php \
// 1>/dev/null

        val commandArgs = buildList {
            interpreter.apply {
                println("interpreter: $this")
                add(this.pathToPhpExecutable!!)
            }
            add("-l")
            add("-ddisplay_errors=0")
            add("-derror_reporting=0")

            add("-dopcache.enable_cli=1")
            add("-dopcache.save_comments=1")
            add("-dopcache.opt_debug_level=0x10000")
            add("-dopcache.optimization_level=0")

            add(file)
        }

        CoroutineScope(Dispatchers.IO).launch {
            executeCommand(commandArgs)
            callback()
        }
    }

    private suspend fun executeCommand(commandArgs: List<String>) = withContext(Dispatchers.IO) {
        val command = GeneralCommandLine(commandArgs)
        command.withRedirectErrorStream(false)

        val commandLine = command.commandLineString + " 1>/dev/null"
        val processHandler = KillableColoredProcessHandler.Silent(command.createProcess(), commandLine, command.charset, emptySet())
        processHandler.setShouldKillProcessSoftly(false)
        processHandler.setShouldDestroyProcessRecursively(true)

        consoleView?.clear()
        consoleView?.attachToProcess(processHandler)
//        consoleView?.requestScrollingToEnd()

        processHandler.startNotify()
    }
}