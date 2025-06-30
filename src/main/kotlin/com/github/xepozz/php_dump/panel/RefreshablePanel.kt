package com.github.xepozz.php_dump.panel

import com.intellij.openapi.project.Project

interface RefreshablePanel {
    fun refresh(project: Project, type: RefreshType)
}
enum class RefreshType {
    AUTO, MANUAL
}