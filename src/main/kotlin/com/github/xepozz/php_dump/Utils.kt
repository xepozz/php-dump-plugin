package com.github.xepozz.php_dump

import com.intellij.ide.plugins.PluginManagerCore.isUnitTestMode
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.Callable


inline fun <R> Project.nonBlocking(crossinline block: () -> R, crossinline uiContinuation: (R) -> Unit) {
    if (isUnitTestMode) {
        val result = block()
        uiContinuation(result)
    } else {
        ReadAction.nonBlocking(Callable { block() })
            .inSmartMode(this)
            .expireWith(DumpPluginDisposable.getInstance(this))
            .finishOnUiThread(ModalityState.current()) { uiContinuation(it) }
            .submit(AppExecutorUtil.getAppExecutorService())
    }
}

@Service(Service.Level.PROJECT)
class DumpPluginDisposable : Disposable {
    companion object {
        @JvmStatic
        fun getInstance(project: Project): Disposable = project.getService(DumpPluginDisposable::class.java)
    }

    override fun dispose() {}
}
