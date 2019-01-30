package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.data.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IChampionInteractor
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IRiotData
import io.reactivex.disposables.Disposable

class DataManager(context: Context) : IRiotData, IChampionInteractor, Disposable {
    override val shouldRefresh: Boolean
        get() = preferenceManager.isOutdated

    private val database = DatabaseManager
    private val fileStorage = FileStorage
    private val preferenceManager = PreferenceManager
    private val resourceManager = ResourceManager

    private val disposables = mutableListOf<Disposable>()

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

    override fun findChampion(championKey: Int, consumer: (Champion) -> Unit) {
        disposables += database.findChampion(consumer, championKey)
    }

    override fun findRandomChampion(consumer: (Champion) -> Unit, championKey: Int?, role: String?) {
        disposables += database.findRandomChampion(consumer, championKey, role)
    }

    override fun findChampionList(consumer: (Collection<Champion>) -> Unit, role: String?) {
        disposables += database.findChampionList(consumer, role)
    }

    override fun isDisposed(): Boolean = disposables.all(Disposable::isDisposed)

    override fun dispose() = disposables.onEach(Disposable::dispose).clear()
}