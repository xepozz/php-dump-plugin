package com.github.xepozz.php_dump.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class OpcodesDumperService(var project: Project) : Disposable, DumperServiceInterface {
    var consoleView: ConsoleView? = null

    val state = DebugLevelState.getInstance(project)

    override fun dispose() {
        consoleView?.dispose()
    }

    override suspend fun dump(file: String) {
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

        val interpreterPath = interpreter.pathToPhpExecutable ?: return
        val debugLevel = maxOf(1, minOf(2, state.debugLevel))
        val preloadFile = state.preloadFile

        val commandArgs = buildList {
            add(interpreterPath)
            add("-l")
            add("-ddisplay_errors=0")
            add("-derror_reporting=0")

            add("-dopcache.enable_cli=1")
            add("-dopcache.save_comments=1")
            add("-dopcache.opt_debug_level=0x${debugLevel}0000")
            add("-dopcache.optimization_level=0")
            if (preloadFile != null) {
                add("-dopcache.preload=${preloadFile}")
            }

            add(file)
        }

        CoroutineScope(Dispatchers.IO).launch {
            executeCommand(commandArgs)
        }
    }

    private suspend fun executeCommand(commandArgs: List<String>) = withContext(Dispatchers.IO) {
        val command = GeneralCommandLine(commandArgs)
        command.withRedirectErrorStream(false)

//        println("running command ${command.commandLineString}")
        val processHandler = KillableColoredProcessHandler.Silent(command)
        processHandler.setShouldKillProcessSoftly(false)
        processHandler.setShouldDestroyProcessRecursively(true)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                if (outputType == ProcessOutputTypes.STDERR) {
                    consoleView?.print(event.text, ConsoleViewContentType.NORMAL_OUTPUT)
                }
            }
        })

        consoleView?.clear()
//        consoleView?.attachToProcess(processHandler)
//        consoleView?.requestScrollingToEnd()

        processHandler.startNotify()
    }
}
