package com.spiderbiggen.randomchampionselector.domain.champions.usecase

import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import kotlinx.coroutines.flow.Flow

interface UpdateChampions {
    suspend fun update(force: Boolean = false): Flow<DownloadProgress>
}