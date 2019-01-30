package com.spiderbiggen.randomchampionselector.model

import com.spiderbiggen.randomchampionselector.domain.Champion

interface IChampionInteractor {

    suspend fun findChampion(championKey: Int): Champion

    suspend fun findRandomChampion(championKey: Int? = null, role: String? = null): Champion

    suspend fun findChampionList(role: String? = null): Collection<Champion>

}