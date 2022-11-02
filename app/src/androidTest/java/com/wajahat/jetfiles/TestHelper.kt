package com.wajahat.jetfiles

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.wajahat.jetfiles.data.AppContainerImpl
import com.wajahat.jetfiles.ui.JetFilesApp

/**
 * Launches the app from a test context.
 */
fun ComposeContentTestRule.launchJetFilesApp() {
    setContent {
        JetFilesApp(
            AppContainerImpl()
        )
    }
}