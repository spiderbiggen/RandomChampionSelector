package com.spiderbiggen.randomchampionselector.data.storage.repositories

import com.spiderbiggen.randomchampionselector.data.mappers.ChampionMapper
import com.spiderbiggen.randomchampionselector.data.storage.database.daos.ChampionDAO
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChampionDataRepository @Inject constructor(
    private val championDAO: ChampionDAO,
    private val championMapper: ChampionMapper,
) : ChampionRepository {
    override val champions: Flow<List<Champion>> =
        championDAO.getAll().map { it.map(championMapper::fromDatabase) }

    override suspend fun currentChampions(): List<Champion> =
        championDAO.getCurrent().map(championMapper::fromDatabase)

    override suspend fun randomChampion(): Champion? =
        championDAO.getRandom()?.let(championMapper::fromDatabase)

    override suspend fun addChampions(champions: Collection<Champion>) =
        championDAO.insertAll(champions.map(championMapper::toDatabase))

    override suspend fun getChampion(id: Int): Champion? =
        championDAO.getChampion(id)?.let(championMapper::fromDatabase)
}