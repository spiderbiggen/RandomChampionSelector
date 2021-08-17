package com.spiderbiggen.randomchampionselector.util.data.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.storage.database.converters.StringArrayConverter
import com.spiderbiggen.randomchampionselector.util.data.storage.database.daos.ChampionDAO
import kotlinx.coroutines.flow.Flow

@Database(entities = [(Champion::class)], version = 4, exportSchema = false)
@TypeConverters(StringArrayConverter::class)
abstract class SimpleDatabase : RoomDatabase() {
    abstract fun championDAO(): ChampionDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SimpleDatabase? = null

        fun getDatabase(context: Context): SimpleDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    SimpleDatabase::class.java,
                    "random_champion_main"
                )
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}
