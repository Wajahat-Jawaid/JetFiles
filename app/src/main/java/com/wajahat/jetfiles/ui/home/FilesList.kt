package com.wajahat.jetfiles.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wajahat.jetfiles.model.JetFile

/**
 * Display all the files.
 *
 * When a file is clicked on, [onSelectFile] will be called.
 *
 * @param filesList files to display
 * @param state LazyColumn's current scrolled state
 */
@Composable
fun FilesList(
    modifier: Modifier = Modifier,
    filesList: List<JetFile>,
    favorites: Set<String>,
    onSelectFile: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    state: LazyListState
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = state
    ) {
        items(items = filesList, itemContent = { file ->
            FileCard(
                file = file,
                onSelectFile = onSelectFile,
                isFavorite = favorites.contains(file.path),
                onToggleFavorite = { onToggleFavorite(file.path) }
            )
            FileListDivider()
        })
    }
}