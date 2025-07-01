package com.github.xepozz.php_dump.configuration

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "com.github.xepozz.php_dump.services.PhpDumpSettingsService",
    storages = [Storage("PhpDump.xml")]
)
class PhpDumpSettingsService : SimplePersistentStateComponent<PhpDumpSettingsService.State>(State()) {
    class State : BaseState() {
        var debugLevel: Int by property(1)
        var preloadFile: String? by string(null)
        var autoRefresh: Boolean by property(true)
        var tokensObject: Boolean by property(true)
    }

    var debugLevel: Int by state::debugLevel
    var preloadFile: String? by state::preloadFile
    var autoRefresh: Boolean by state::autoRefresh

    var tokensObject: Boolean by state::autoRefresh

    companion object {
        fun getInstance(project: Project): PhpDumpSettingsService {
            return project.getService(PhpDumpSettingsService::class.java)
        }
    }
}