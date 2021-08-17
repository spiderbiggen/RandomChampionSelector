package com.spiderbiggen.randomchampionselector.util.data.storage.repositories

import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.storage.database.daos.ChampionDAO
import kotlinx.coroutines.flow.Flow

class ChampionRepository(private val championDAO: ChampionDAO) {
    val allChampions: Flow<List<Champion>> = championDAO.getAll()
    suspend fun randomChampion(): Champion? = championDAO.getRandom()

    suspend fun addChampions(champions: Collection<Champion>) {
        championDAO.insertAll(champions)
    }

    suspend fun getChampion(id: Int): Champion? {
        return championDAO.getChampion(id)
    }
}