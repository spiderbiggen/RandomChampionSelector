package com.spiderbiggen.randomchampionselector.util.data.storage.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spiderbiggen.randomchampionselector.models.Champion

/**
 * Tells room what methods are used to access [Champion] objects in the database.
 *
 * @author Stefan Breetveld
 */
@Dao
interface ChampionDAO {

    /**
     * Retrieve all possible values for roles in the champion Object.
     * These roles need to be split up to get individual roles.
     *
     * @return a list of all possible joined roles.
     */
    @Query("SELECT DISTINCT roles FROM champion")
    suspend fun getAllRoles(): List<String>

    /**
     * Retrieve all champions that have the given role. If no role is specified retrieve all champions.
     *
     * @param role the requested role
     * @return a list of champions matching [role]
     */
    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY name")
    suspend fun getAll(role: String?): List<Champion>

    /**
     * Retrieve a random champion that has the given role. If no role is specified retrieve any champion.
     *
     * @param role the request role
     * @return a champion matching [role]
     */
    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(role: String?): Champion

    /**
     * Retrieve the champion with the given [id].
     *
     * @param id the id of the desired champion
     * @return the champion that matches [id]
     */
    @Query("SELECT * FROM champion WHERE `key` = :id")
    suspend fun getChampion(id: Int): Champion

    /**
     * Insert all the champions given by [entities]. If a conflict is found the new object replaces the old one.
     *
     * @param entities the updated champions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Collection<Champion>)
}
