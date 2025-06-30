package com.github.xepozz.php_dump.services

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "com.github.xepozz.php_dump.services.DebugLevelState",
    storages = [Storage("PhpDump.xml")]
)
class DebugLevelState : SimplePersistentStateComponent<DebugLevelState.State>(State()) {
    class State : BaseState() {
        var debugLevel: Int by property(1)
        var preloadFile: String? by string(null)
        var autoRefresh: Boolean by property(true)
    }

    var debugLevel: Int by state::debugLevel
    var preloadFile: String? by state::preloadFile
    var autoRefresh: Boolean by state::autoRefresh

    companion object {
        fun getInstance(project: Project): DebugLevelState {
            return project.getService(DebugLevelState::class.java)
        }
    }
}