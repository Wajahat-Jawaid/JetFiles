package com.wajahat.jetfiles.ui.home

import android.os.Environment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wajahat.jetfiles.R
import com.wajahat.jetfiles.data.Result
import com.wajahat.jetfiles.data.file.FileRepository
import com.wajahat.jetfiles.model.JetFile
import com.wajahat.jetfiles.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * UI state for the Home screen.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * No files to display either because they are loading, not present or maybe failed to load.
     * In actual project, we should have a generic file which would be extended by all the individual states
     * across the module/project
     */
    data class NoFiles(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState

    /**
     * Display the files provided by the dataSource. In actual project, the above mentioned strategy should be
     * adopted
     */
    data class HasFiles(
        val filesList: List<JetFile>,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState
}

/**
 * A Single source of truth representation that acts as the input to decide which uiState to render
 */
private data class HomeViewModelState(
    val jetFiles: List<JetFile>? = null,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving the ui.
     */
    fun toUiState(): HomeUiState =
        if (jetFiles == null) {
            HomeUiState.NoFiles(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasFiles(
                filesList = jetFiles,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

class HomeViewModel(private val fileRepository: FileRepository) : ViewModel(), DefaultLifecycleObserver {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    // UI state exposed to the Screens
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value.toUiState()
        )

    init {
        refreshFiles()

        // Observe for favorite changes in the repo layer
        viewModelScope.launch {
            fileRepository.observeFavorites().collect { favorites ->
                viewModelState.update { it.copy(favorites = favorites) }
            }
        }
    }


    /**
     * Refresh files and update the UI state accordingly
     */
    fun refreshFiles() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Provide the data source of the files. For the demo purpose, I've fetched the files from
            // Downloads directory. You can change to whatever you want
            val result = fileRepository.getJetFiles(downloadsDirectory)
            viewModelState.update {
                when (result) {
                    is Result.Success -> {
                        // Filter the file names against the searchInput
                        val filteredFiles = result.data.filter { file ->
                            file.name.contains(viewModelState.value.searchInput, ignoreCase = true)
                        }
                        it.copy(jetFiles = filteredFiles, isLoading = false)
                    }
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    fun toggleFavourite(fileUri: String) {
        viewModelScope.launch {
            fileRepository.toggleFavorite(fileUri)
        }
    }

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
        refreshFiles()
    }

    /**
     * Factory for HomeViewModel that takes [FileRepository] as a dependency
     */
    companion object {
        private val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        fun provideFactory(fileRepository: FileRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(fileRepository) as T
                }
            }
    }
}