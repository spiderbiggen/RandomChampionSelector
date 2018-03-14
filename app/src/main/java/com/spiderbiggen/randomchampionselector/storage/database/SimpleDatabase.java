package com.spiderbiggen.randomchampionselector.storage.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.converters.StringArrayConverter;
import com.spiderbiggen.randomchampionselector.storage.database.daos.ChampionDAO;

@Database(entities = {Champion.class}, version = 1, exportSchema = false)
@TypeConverters({StringArrayConverter.class})
public abstract class SimpleDatabase extends RoomDatabase {
    public abstract ChampionDAO championDAO();
}
