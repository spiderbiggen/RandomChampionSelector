package com.spiderbiggen.randomchampionselector.data.storage.database

import com.spiderbiggen.randomchampionselector.domain.Champion

/**
 * Defines all interactions that load data.
 *
 * @author Stefan Breetveld
 */
interface IDataInteractor {

    suspend fun addChampion(champion: Champion)

    suspend fun addChampions(champions: Collection<Champion>)

    suspend fun removeChampion(champion: Champion)

    suspend fun removeChampions(champions: Collection<Champion>)

    suspend fun updateChampion(champion: Champion)

    suspend fun updateChampions(champions: Collection<Champion>)

    suspend fun findRoleList(): List<String>

    suspend fun findChampion(championKey: Int): Champion

    suspend fun findChampionList(role: String? = null): List<Champion>

    suspend fun findRandomChampion(championKey: Int? = null, role: String? = null): Champion

}
