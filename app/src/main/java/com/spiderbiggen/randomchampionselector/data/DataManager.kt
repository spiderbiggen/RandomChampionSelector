package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.data.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IChampionInteractor
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IRiotData

class DataManager(context: Context) : IRiotData, IChampionInteractor {
    override val shouldRefresh: Boolean
        get() = preferenceManager.isOutdated

    private val database = DatabaseManager
    private val fileStorage = FileStorage
    private val preferenceManager = PreferenceManager
    private val resourceManager = ResourceManager


    init {
        database.useContext(context)
        fileStorage.useContext(context)
        preferenceManager.useContext(context)
        resourceManager.useContext(context)
    }

    override suspend fun verifyImages(progress: IProgressCallback) {
        val champions = DDragon.getChampionList()
        val things = DDragon.verifyImages(champions, progress)
        DDragon.downloadAllImages(things, progress)
    }

    override suspend fun update(progress: IProgressCallback) {
            progress.onProgressUpdate(IProgressCallback.Progress.CHECKING_VERSION)
            DDragon.updateVersion()
            val champions = DDragon.getChampionList()
            val things = DDragon.verifyImages(champions, progress)
            DDragon.downloadAllImages(things, progress)
    }

    override suspend fun findChampion(championKey: Int): Champion {
        return database.findChampion(championKey)
    }

    override suspend fun findRandomChampion(championKey: Int?, role: String?): Champion {
        return database.findRandomChampion(championKey, role)
    }

    override suspend fun findChampionList(role: String?): Collection<Champion> {
        return database.findChampionList(role)
    }

}