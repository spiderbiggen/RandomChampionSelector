package com.spiderbiggen.randomchampionselector.util.data.storage.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spiderbiggen.randomchampionselector.models.Champion
import kotlinx.coroutines.flow.Flow

/**
 * Tells room what methods are used to access [Champion] objects in the database.
 *
 * @author Stefan Breetveld
 */
@Dao
interface ChampionDAO {

    /**
     * Retrieve all champions.
     *
     * @return a list of champions
     */
    @Query("SELECT * FROM champion ORDER BY name")
    fun getAll(): Flow<List<Champion>>

    /**
     * Retrieve a random champion.
     *
     * @return a champion
     */
    @Query("SELECT * FROM champion ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): Champion?

    /**
     * Retrieve the champion with the given [id].
     *
     * @param id the id of the desired champion
     * @return the champion that matches [id]
     */
    @Query("SELECT * FROM champion WHERE `key` = :id")
    suspend fun getChampion(id: Int): Champion?

    /**
     * Insert all the champions given by [entities]. If a conflict is found the new object replaces the old one.
     *
     * @param entities the updated champions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: Collection<Champion>)
}
