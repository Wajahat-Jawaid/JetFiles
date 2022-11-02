package com.wajahat.jetfiles

import android.app.Application
import com.wajahat.jetfiles.data.AppContainer
import com.wajahat.jetfiles.data.AppContainerImpl

class JetFilesApplication : Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl()
    }
}