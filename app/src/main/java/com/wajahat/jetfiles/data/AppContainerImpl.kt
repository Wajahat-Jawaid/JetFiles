package com.wajahat.jetfiles.data

import com.wajahat.jetfiles.data.file.FileRepository
import com.wajahat.jetfiles.data.file.FileRepositoryImpl

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val fileRepository: FileRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl : AppContainer {

    override val fileRepository: FileRepository by lazy {
        FileRepositoryImpl()
    }
}