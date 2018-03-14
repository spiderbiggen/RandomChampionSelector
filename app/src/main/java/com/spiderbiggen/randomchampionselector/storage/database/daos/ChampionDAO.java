package com.spiderbiggen.randomchampionselector.storage.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.Collection;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * On 26-2-2018.
 *
 * @author Stefan Breetveld
 */
@Dao
public interface ChampionDAO {

    @Query("SELECT * FROM champion WHERE roles LIKE :role||'%' ORDER BY name")
    Flowable<List<Champion>> getAll(String role);

    @Query("SELECT * FROM champion ORDER BY name")
    Flowable<List<Champion>> getAll();

    @Query("SELECT DISTINCT roles FROM champion")
    Flowable<List<String>> getAllRoles();

    @Query("SELECT * FROM champion WHERE roles LIKE :role||'%' ORDER BY RANDOM() LIMIT 1")
    Flowable<Champion> getRandom(String role);

    @Query("SELECT * FROM champion ORDER BY RANDOM() LIMIT 1")
    Flowable<Champion> getRandom();

    @Query("SELECT * FROM champion WHERE `key` = :id")
    Flowable<Champion> getChampion(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Collection<Champion> entities);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(Collection<Champion> entities);

    default void insertAll(Collection<Champion> entities) {
        insert(entities);
        update(entities);
    }

    @Delete
    void delete(Champion champion);

}
