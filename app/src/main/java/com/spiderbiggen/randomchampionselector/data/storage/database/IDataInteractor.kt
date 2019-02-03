package com.spiderbiggen.randomchampionselector.data.storage.database

import com.spiderbiggen.randomchampionselector.domain.Champion

/**
 * Defines all interactions with the database.
 *
 * @author Stefan Breetveld
 */
interface IDataInteractor {

    /**
     * Insert/replace all the champions given by [champions].
     *
     * @param champions the updated champions
     */
    suspend fun addChampions(champions: Collection<Champion>)

    /**
     * Retrieve all possible values for roles in the champion Object.
     *
     * @return a list of all possible roles.
     */
    suspend fun findRoleList(): List<String>

    /**
     * Retrieve the champion with the given [championKey].
     *
     * @param championKey the championKey of the desired champion
     * @return the champion that matches [championKey]
     */
    suspend fun findChampion(championKey: Int): Champion

    /**
     * Retrieve all champions that have the given role. If no role is specified retrieve all champions.
     *
     * @param role the requested role
     * @return a list of champions matching [role]
     */
    suspend fun findChampionList(role: String? = null): List<Champion>

    /**
     * Retrieve a random champion that has the given role. If no role is specified retrieve any champion.
     *
     * @param role the request role
     * @return a champion matching [role]
     */
    suspend fun findRandomChampion(role: String? = null): Champion

}
