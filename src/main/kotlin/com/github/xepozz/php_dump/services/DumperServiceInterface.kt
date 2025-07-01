package com.github.xepozz.php_dump.services

import com.intellij.openapi.vfs.VirtualFile

interface DumperServiceInterface {
    suspend fun dump(file: VirtualFile): Any? {
        return dump(file.path)
    }

    suspend fun dump(file: String): Any?
}