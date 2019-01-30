package com.spiderbiggen.randomchampionselector.data.storage.database.daos

import androidx.room.*

import com.spiderbiggen.randomchampionselector.domain.Champion

/**
 * On 26-2-2018.
 *
 * @author Stefan Breetveld
 */
@Dao
interface ChampionDAO {

    @Query("SELECT DISTINCT roles FROM champion")
    fun getAllRoles(): List<String>

    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY name")
    fun getAll(role: String?): List<Champion>

    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY RANDOM() LIMIT 1")
    fun getRandom(role: String?): Champion

    @Query("SELECT * FROM champion WHERE `key` = :id")
    fun getChampion(id: Int): Champion

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: Collection<Champion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: Champion)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAll(entities: Collection<Champion>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(entity: Champion)

    @Delete
    suspend fun delete(champion: Champion)

    @Delete
    suspend fun deleteAll(champion: Collection<Champion>)

}
