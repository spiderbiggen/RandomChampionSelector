package com.spiderbiggen.randomchampionselector.views

import android.app.Application
import androidx.room.Room
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.data.ResourceManager
import com.spiderbiggen.randomchampionselector.data.storage.database.IDataInteractor
import com.spiderbiggen.randomchampionselector.data.storage.database.SimpleDatabase
import com.spiderbiggen.randomchampionselector.data.storage.file.FileStorage

/**
 * Custom implementation of [Application] to make sure the different managers and the database are initialized
 *
 * @author Stefan Breetveld
 */
class DataApplication : Application() {
    val database: IDataInteractor by lazy {
        Room.databaseBuilder(applicationContext, SimpleDatabase::class.java, "random_champion_main")
            .fallbackToDestructiveMigration().build()
    }

    override fun onCreate() {
        super.onCreate()
        FileStorage.useContext(this)
        PreferenceManager.useContext(this)
        ResourceManager.useContext(this)
    }
}