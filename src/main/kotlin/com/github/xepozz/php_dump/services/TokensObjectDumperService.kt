package com.github.xepozz.php_dump.services

import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key

@Service(Service.Level.PROJECT)
class TokensObjectDumperService(var project: Project) : Disposable, DumperServiceInterface {
    var consoleView: ConsoleView? = null

    override fun dispose() {
        consoleView?.dispose()
    }

    override fun dump(file: String) {
        // language=injectablephp
        val phpSnippet = $$"""
            print_r(
                array_map(
                    function (PhpToken $token) {
                        return [
                            'line' => $token->line,
                            'pos' => $token->pos,
                            'name' => $token->getTokenName(),
                            'value' => $token->text,
                        ];
                    },
                    \PhpToken::tokenize(file_get_contents($argv[1])),
                )
            );
        """.trimIndent()

        consoleView?.clear()
        PhpCommandExecutor.execute(file, phpSnippet, project, object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                when (outputType) {
                    ProcessOutputTypes.STDERR -> consoleView?.print(event.text, ConsoleViewContentType.ERROR_OUTPUT)
                    ProcessOutputTypes.STDOUT -> consoleView?.print(event.text, ConsoleViewContentType.NORMAL_OUTPUT)
                }
            }
        })
    }
}