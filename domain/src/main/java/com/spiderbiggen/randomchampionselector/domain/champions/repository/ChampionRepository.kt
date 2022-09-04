package com.spiderbiggen.randomchampionselector.domain.champions.repository

import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    val champions: Flow<List<Champion>>

    suspend fun currentChampions(): List<Champion>

    suspend fun randomChampion(): Champion?

    suspend fun addChampions(champions: Collection<Champion>)

    suspend fun getChampion(id: Int): Champion?
}