package com.spiderbiggen.randomchampionselector.data.champions.usecase

import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.mappers.ChampionMapper
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.domain.champions.usecase.UpdateChampions
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.*
import javax.inject.Inject

class UpdateDataChampions @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val dDragon: DDragon,
    private val championRepository: ChampionRepository,
    private val championMapper: ChampionMapper,
) : UpdateChampions {

    override suspend fun update(force: Boolean): Flow<DownloadProgress> = channelFlow {
        send(DownloadProgress.Idle)
        try {
            var champions: List<Champion> = listOf()
            var lastVersion = preferenceRepository.version.orEmpty()

            if (!force && !preferenceRepository.isOutdated) {
                champions = championRepository.currentChampions()
            }
            if (champions.isEmpty()) {
                send(DownloadProgress.CheckingVersion)
                val version = dDragon.getLastVersion()

                if (!force && version == preferenceRepository.version) {
                    champions = championRepository.currentChampions()
                }
                lastVersion = version
            }

            if (champions.isEmpty()) {
                send(DownloadProgress.UpdateChampions)
                champions = dDragon.getChampionList(lastVersion).map(championMapper::fromApi)
                championRepository.addChampions(champions)
            }

            dDragon.verifyImages(champions).collect { progress ->
                send(progress)
                (progress as? DownloadProgress.Unvalidated)?.champions?.let { champions = it }
            }

            dDragon.downloadAllImages(champions).collect { progress ->
                send(progress)
            }
            preferenceRepository.lastSync = Date().time
            preferenceRepository.version = lastVersion
            send(DownloadProgress.Success)
        } catch (t: Throwable) {
            send(DownloadProgress.Error(t))
        }
    }
}