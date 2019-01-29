package com.spiderbiggen.randomchampionselector.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.data.storage.database.converters.StringArrayConverter
import com.spiderbiggen.randomchampionselector.data.storage.database.daos.ChampionDAO

@Database(entities = [(Champion::class)], version = 2, exportSchema = false)
@TypeConverters(StringArrayConverter::class)
abstract class SimpleDatabase : RoomDatabase() {
    abstract fun championDAO(): ChampionDAO
}
