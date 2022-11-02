package com.wajahat.jetfiles.data.file

import com.wajahat.jetfiles.data.Result
import com.wajahat.jetfiles.model.JetFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class FileRepositoryImpl : FileRepository {

    // Favorite files data holder
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getJetFiles(parentFile: File): Result<List<JetFile>> {
        // Mutable list of our [PdfFile] objects to render
        val jetFiles = mutableListOf<JetFile>()
        // All files present inside the parent and child directories
        val allFiles = parentFile.listFiles()
        if (allFiles.isNullOrEmpty()) {
            return Result.Error(IllegalArgumentException("No files found"))
        }

        // Loop through all the files to map the native #File to our #JetFile object
        allFiles.forEach { file ->
            // If file is a sub-directory, then dive inside that too
            // Respecting the privacy, we don't want to show/read the hidden files
            file.apply {
                if (isDirectory && !isHidden) {
                    getJetFiles(this)
                } else {
                    // If you want to filter particular types of files like the pdf|txt|jpg, then with the
                    // following check, you can check the file extension or multiple kinds of extensions
                    // if (name.endsWith(".ext")) {
                    // }
                    jetFiles.add(
                        JetFile(
                            name = name,
                            modifiedAt = lastModified(),
                            size = length(),
                            path = absolutePath
                        )
                    )
                }
            }
        }

        return Result.Success(jetFiles)
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(fileUri: String) {
        val set = favorites.value.toMutableSet()
        if (!set.add(fileUri)) {
            set.remove(fileUri)
        }
        favorites.value = set.toSet()
    }
}