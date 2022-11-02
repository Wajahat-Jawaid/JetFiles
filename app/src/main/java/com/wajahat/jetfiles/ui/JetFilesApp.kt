package com.wajahat.jetfiles.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wajahat.jetfiles.data.AppContainer
import com.wajahat.jetfiles.ui.home.HomeScreen
import com.wajahat.jetfiles.ui.home.HomeViewModel
import com.wajahat.jetfiles.ui.theme.JetFilesTheme
import java.io.File

@Composable
fun JetFilesApp(
    appContainer: AppContainer
) {
    JetFilesTheme {
        ProvideWindowInsets {
            // Drawing content behind the system windows. Just a UX thing!
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = true)
            }

            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.fileRepository)
            )
            val context = LocalContext.current

            Scaffold { padding ->
                HomeScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    homeViewModel = homeViewModel,
                    onSelectFile = { onSelectFile(context, it) }
                )
            }
        }
    }
}

fun onSelectFile(context: Context, path: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(path))
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = uri
    context.startActivity(intent)
}