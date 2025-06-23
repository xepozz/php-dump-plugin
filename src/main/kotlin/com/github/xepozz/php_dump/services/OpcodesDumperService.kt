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
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class OpcodesDumperService(var project: Project) : Disposable {
    var consoleView: ConsoleView? = null

    companion object {
        fun dump(file: VirtualFile, project: Project) {
            val service = project.getService(OpcodesDumperService::class.java)

            service.dump(file.path)
        }
    }

    override fun dispose() {
        consoleView?.dispose()
    }

    fun dump(file: String) {
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
        }
    }

    private suspend fun executeCommand(commandArgs: List<String>) = withContext(Dispatchers.IO) {
        val command = GeneralCommandLine(commandArgs)
        command.withRedirectErrorStream(false)

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