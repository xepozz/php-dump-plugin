package com.github.xepozz.php_dump.services

import com.github.xepozz.php_dump.command.PhpCommandExecutor
import com.github.xepozz.php_dump.configuration.PhpDumpSettingsService
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class TokensDumperService(var project: Project) : DumperServiceInterface {
    val state = PhpDumpSettingsService.getInstance(project)

    override suspend fun dump(file: String): Any? {
        val phpSnippet = if (state.tokensObject) {
            // language=injectablephp
            $$"""
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
        } else {
            // language=injectablephp
            $$"""
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
        }

        return withContext(Dispatchers.IO) {
            val output = StringBuilder()

            PhpCommandExecutor.execute(file, phpSnippet, project, object : ProcessAdapter() {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    when (outputType) {
                        ProcessOutputTypes.STDERR -> output.append(event.text)
                        ProcessOutputTypes.STDOUT -> output.append(event.text)
                    }
                }
            })

            return@withContext output.toString()
        }
    }
}