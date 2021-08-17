package com.spiderbiggen.randomchampionselector

import android.app.Application
import androidx.room.Room
import com.spiderbiggen.randomchampionselector.util.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.util.data.ResourceManager
import com.spiderbiggen.randomchampionselector.util.data.storage.database.SimpleDatabase
import com.spiderbiggen.randomchampionselector.util.data.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.util.data.storage.repositories.ChampionRepository

/**
 * Custom implementation of [Application] to make sure the different managers and the database are initialized
 *
 * @author Stefan Breetveld
 */
class DataApplication : Application() {

    val database by lazy { SimpleDatabase.getDatabase(this) }
    val championRepository by lazy {ChampionRepository(database.championDAO())}

    override fun onCreate() {
        super.onCreate()
        FileStorage.useContext(this)
        PreferenceManager.useContext(this)
        ResourceManager.useContext(this)
    }
}