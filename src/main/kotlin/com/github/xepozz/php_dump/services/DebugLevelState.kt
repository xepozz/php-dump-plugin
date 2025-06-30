package com.github.xepozz.php_dump.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "com.github.xepozz.php_dump.services.DebugLevelState",
    storages = [Storage("phpDumpDebugLevel.xml")]
)
class DebugLevelState : PersistentStateComponent<DebugLevelState.State> {
    data class State(
        var debugLevel: Int = 1
    )

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    fun getDebugLevel(): Int {
        return myState.debugLevel
    }

    fun setDebugLevel(level: Int) {
        myState.debugLevel = level
    }

    companion object {
        fun getInstance(project: Project): DebugLevelState {
            return project.getService(DebugLevelState::class.java)
        }
    }
}