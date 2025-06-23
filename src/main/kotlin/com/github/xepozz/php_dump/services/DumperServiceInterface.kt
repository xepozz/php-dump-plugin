package com.github.xepozz.php_dump.services

import com.intellij.openapi.vfs.VirtualFile

interface DumperServiceInterface {
    fun dump(file: VirtualFile) {
        dump(file.path)
    }
    fun dump(file: String)
}