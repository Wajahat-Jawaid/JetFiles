package com.wajahat.jetfiles.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wajahat.jetfiles.R
import com.wajahat.jetfiles.ui.component.FullScreenLoading
import com.wajahat.jetfiles.ui.component.JetFilesTopAppBar
import com.wajahat.jetfiles.ui.component.LoadingContent

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onSelectFile: (String) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    // UiState of the HomeScreen that handles different states of home screen like Loading, Error, Content etc.
    val uiState by homeViewModel.uiState.collectAsState()

    // Construct the lazy list state. This allows the associated state to survive beyond navigation from and to
    // the PdfViewerScreen or any other future screens, and therefore this way we get to preserve the scroll
    // throughout any changes to the content.
    val lazyListState = rememberLazyListState()

    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onRefreshFiles = { homeViewModel.refreshFiles() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        snackBarHostState = snackBarHostState,
        onSearchInputChanged = { homeViewModel.onSearchInputChanged(it) }
    ) { hasFilesUiState, contentModifier ->
        FilesList(
            modifier = contentModifier,
            filesList = hasFilesUiState.filesList,
            favorites = hasFilesUiState.favorites,
            onSelectFile = onSelectFile,
            onToggleFavorite = { homeViewModel.toggleFavourite(it) },
            state = lazyListState
        )
    }
}

/**
 * Responsible for displaying the Home Screen of this application.
 */
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onRefreshFiles: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    snackBarHostState: SnackbarHostState,
    onSearchInputChanged: (String) -> Unit,
    hasFilesContent: @Composable (
        uiState: HomeUiState.HasFiles, modifier: Modifier
    ) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(Color.Green)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            scaffoldState = rememberScaffoldState(),
            topBar = {
                HomeAppBar(
                    onSearchInputChanged = onSearchInputChanged
                )
            }
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)

            LoadingContent(empty = when (uiState) {
                is HomeUiState.HasFiles -> false
                is HomeUiState.NoFiles -> uiState.isLoading
            },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefreshFiles,
                content = {
                    when (uiState) {
                        // Render the files here
                        is HomeUiState.HasFiles -> hasFilesContent(uiState, contentModifier)
                        is HomeUiState.NoFiles -> {
                            if (uiState.errorMessages.isEmpty()) {
                                // if there are no files, and no error, let the user refresh manually
                                TextButton(
                                    // Cover the refresh view half of width and height
                                    modifier = modifier.fillMaxSize(0.5f),
                                    onClick = onRefreshFiles
                                ) {
                                    Text(
                                        stringResource(id = R.string.tap_to_load_content),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                // there's currently an error showing, don't show any content
                                Box(contentModifier.fillMaxSize())
                            }
                        }
                    }
                })
        }

        // Process first error message and display that on the SnackBar
        if (uiState.errorMessages.isNotEmpty()) {
            // Remember the errorMessage to display on the screen
            val errorMessage = remember(uiState) { uiState.errorMessages[0] }

            // Get the text to show on the message from resources
            val errorMessageText = stringResource(errorMessage.messageId)
            val retryMessageText = stringResource(id = R.string.retry)

            // If onRefreshFiles or onErrorDismiss change while the LaunchedEffect is running,
            // don't restart the effect and use the latest lambda values.
            val onRefreshPostsState by rememberUpdatedState(onRefreshFiles)
            val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

            // Effect running in a coroutine that displays the SnackBar on the screen
            // If there's a change to errorMessageText, retryMessageText or snackBarHostState,
            // the previous effect will be cancelled and a new one will start with the new values
            LaunchedEffect(errorMessageText, retryMessageText, snackBarHostState) {
                val snackBarResult = snackBarHostState.showSnackbar(
                    message = errorMessageText, actionLabel = retryMessageText
                )
                if (snackBarResult == SnackbarResult.ActionPerformed) {
                    onRefreshPostsState()
                }
                // Once the message is displayed and dismissed, notify the ViewModel
                onErrorDismissState(errorMessage.id)
            }
        }
    }
}

/**
 * Top bar that acts as the entry point to decide whether we want to display the search bar or TopBar
 * */
@Composable
fun HomeAppBar(
    onSearchInputChanged: (String) -> Unit
) {
    // Tracking the current state of the search bar
    var searchWidgetState by remember {
        mutableStateOf(SearchWidgetState.CLOSED)
    }
    when (searchWidgetState) {
        // If Search Bar is closed, show the regular TopBar
        SearchWidgetState.CLOSED -> {
            JetFilesTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        searchWidgetState = SearchWidgetState.OPENED
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                }
            )
        }
        // If Search Bar is active, hide the TopBar and display our own SearchAppBar
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                onSearchInputChanged = onSearchInputChanged,
                onCloseClicked = { searchWidgetState = SearchWidgetState.CLOSED },
            )
        }
    }
}

/**
 * Search widget where user can type the search query
 * */
@Composable
fun SearchAppBar(
    onSearchInputChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
) {
    var text by remember {
        mutableStateOf("")
    }

    // State to keep track of whether the keyboard is opened or not
    val showKeyboard = remember { mutableStateOf(true) }
    // Assignable to the TextField
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = Color.White,
        contentColor = contentColorFor(backgroundColor = Color.White)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = text,
            onValueChange = {
                text = it
                onSearchInputChanged(text)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_here)
                )
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
        )

        // LaunchedEffect prevents endless focus request
        LaunchedEffect(focusRequester) {
            if (showKeyboard.value) {
                focusRequester.requestFocus()
            }
        }
    }
}