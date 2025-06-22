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
class TokensDumperService(var project: Project) : Disposable {
    var consoleView: ConsoleView? = null

    companion object {
        fun dump(file: VirtualFile, project: Project) {
            val service = project.getService(TokensDumperService::class.java)

            service.dump(file.path)
        }
    }

    override fun dispose() {
        consoleView?.dispose()
    }

    fun dump(file: String, callback: () -> Unit = {}) {
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

        // language=injectablephp
        val phpSnippet = $$"""
            print_r(
                array_map(
                    function ($token) {
                        return [
                            'line' => $isArray = is_array($token) ? $token[2] : null,
                            'name' => $isArray ? token_name($token[0]) : null,
                            'value' => $isArray ? $token[1] : $token,
                        ];
                    },
                    token_get_all(
                        file_get_contents($argv[1])
                    ),
                )
            );
        """.trimIndent()

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
            executeCommand(commandArgs)
            callback()
        }
    }

    private suspend fun executeCommand(commandArgs: List<String>) = withContext(Dispatchers.IO) {
        val command = GeneralCommandLine(commandArgs)
        command.withRedirectErrorStream(false)

        println("running command ${command.commandLineString}")
        val processHandler = KillableColoredProcessHandler.Silent(command)
        processHandler.setShouldKillProcessSoftly(false)
        processHandler.setShouldDestroyProcessRecursively(true)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                when (outputType) {
                    ProcessOutputTypes.STDERR -> consoleView?.print(event.text, ConsoleViewContentType.ERROR_OUTPUT)
                    ProcessOutputTypes.STDOUT -> consoleView?.print(event.text, ConsoleViewContentType.NORMAL_OUTPUT)
                }
            }
        })

        consoleView?.clear()
//        consoleView?.attachToProcess(processHandler)
//        consoleView?.requestScrollingToEnd()

        processHandler.startNotify()
    }
}