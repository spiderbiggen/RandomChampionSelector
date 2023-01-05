package com.spiderbiggen.randomchampionselector.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.spiderbiggen.randomchampionselector.data.storage.database.converters.StringListConverter
import com.spiderbiggen.randomchampionselector.data.storage.database.daos.ChampionDAO
import com.spiderbiggen.randomchampionselector.data.storage.database.models.ChampionEntity

@Database(entities = [(ChampionEntity::class)], version = 4, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class SimpleDatabase : RoomDatabase() {
    abstract fun championDAO(): ChampionDAO
}

class SimpleDatabaseDecorator(private val database: SimpleDatabase) {
    fun championDao() = database.championDAO()
}
