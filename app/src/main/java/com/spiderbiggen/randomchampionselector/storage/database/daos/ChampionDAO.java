package com.spiderbiggen.randomchampionselector.storage.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.List;

/**
 * On 26-2-2018.
 *
 * @author Stefan Breetveld
 */
@Dao
public interface ChampionDAO {

    @Query("SELECT * FROM champion WHERE roles LIKE :role||'%' ORDER BY name")
    List<Champion> getAll(String role);

    @Query("SELECT * FROM champion ORDER BY name")
    List<Champion> getAll();

    @Query("SELECT DISTINCT roles FROM champion")
    List<String> getAllRoles();

    @Query("SELECT * FROM champion WHERE roles LIKE :role||'%' ORDER BY RANDOM() LIMIT 1")
    Champion getRandom(String role);

    @Query("SELECT * FROM champion ORDER BY RANDOM() LIMIT 1")
    Champion getRandom();

    @Query("SELECT * FROM champion WHERE id = :id")
    Champion getChampion(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Champion... champions);

    @Delete
    void delete(Champion champion);

}
