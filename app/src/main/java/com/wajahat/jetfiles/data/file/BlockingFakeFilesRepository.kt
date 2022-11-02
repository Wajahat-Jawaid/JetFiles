package com.wajahat.jetfiles.data.file

import com.wajahat.jetfiles.data.Result
import com.wajahat.jetfiles.model.JetFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * Implementation of [FileRepository] that returns a hardcoded data for the testing purpose
 */
class BlockingFakeFilesRepository : FileRepository {

    override suspend fun getJetFiles(parentFile: File): Result<List<JetFile>> {
        return Result.Error(IllegalArgumentException("Can't find any files"))
    }

    /** You can return some static favorite objects for testing */
    override fun observeFavorites(): Flow<Set<String>> {
        return flow { }
    }

    /** Create a dummy data set and play with that by toggling on/off the favorites */
    override suspend fun toggleFavorite(fileUri: String) {
    }
}