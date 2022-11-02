package com.wajahat.jetfiles

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wajahat.jetfiles.data.file.BlockingFakeFilesRepository
import com.wajahat.jetfiles.ui.home.HomeScreen
import com.wajahat.jetfiles.ui.home.HomeViewModel
import com.wajahat.jetfiles.ui.theme.JetFilesTheme
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Checks that the Snackbar is shown when the HomeScreen data contains an error.
     */
    @Test
    fun filesContainError_snackbarShown() {
        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            JetFilesTheme {
                // When the Home screen receives data with an error
                HomeScreen(
                    homeViewModel = HomeViewModel(BlockingFakeFilesRepository()),
                    onSelectFile = { },
                    snackBarHostState = snackbarHostState
                )
            }
        }

        // Then the first message received in the Snackbar is an error message
        runBlocking {
            // snapshotFlow converts a State to a Kotlin Flow so we can observe it
            // wait for the first a non-null `currentSnackbarData`
            val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
                .filterNotNull().first().message
            val expectedSnackbarText = InstrumentationRegistry.getInstrumentation()
                .targetContext.resources.getString(R.string.load_error)
            assertEquals(expectedSnackbarText, actualSnackbarText)
        }
    }
}