package com.spiderbiggen.randomchampionselector.data.storage.database.daos

import androidx.room.*

import com.spiderbiggen.randomchampionselector.domain.Champion

import io.reactivex.Flowable

/**
 * On 26-2-2018.
 *
 * @author Stefan Breetveld
 */
@Dao
interface ChampionDAO {

    @get:Query("SELECT * FROM champion ORDER BY name")
    val all: Flowable<List<Champion>>

    @get:Query("SELECT DISTINCT roles FROM champion")
    val allRoles: Flowable<List<String>>

    @get:Query("SELECT * FROM champion ORDER BY RANDOM() LIMIT 1")
    val random: Flowable<Champion>

    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY name")
    fun getAll(role: String?): Flowable<List<Champion>>

    @Query("SELECT * FROM champion WHERE roles LIKE '%'||:role||'%' ORDER BY RANDOM() LIMIT 1")
    fun getRandom(role: String?): Flowable<Champion>

    @Query("SELECT * FROM champion WHERE `key` = :id")
    fun getChampion(id: Int): Flowable<Champion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: Collection<Champion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: Champion)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAll(entities: Collection<Champion>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(entity: Champion)

    @Delete
    fun delete(champion: Champion)

    @Delete
    fun deleteAll(champion: Collection<Champion>)

}
