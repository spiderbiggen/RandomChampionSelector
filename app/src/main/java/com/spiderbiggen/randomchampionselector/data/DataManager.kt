package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import android.util.Log
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.ddragon.ImageDescriptor
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
    private val riotApi = DDragon
    private val disposables = mutableListOf<Disposable>()

    init {
        if (!database.hasContext()) {
            database.useContext(context)
        }
        if (!fileStorage.hasContext()) {
            fileStorage.useContext(context)
        }
        if (!preferenceManager.hasContext()) {
            preferenceManager.useContext(context)
        }
        if (!resourceManager.hasContext()) {
            resourceManager.useContext(context)
        }
    }

    override fun verifyImages(progress: IProgressCallback, finished: IRiotData.OnFinished) {
        Verify(progress, finished, database, riotApi).run()
    }

    override fun update(progress: IProgressCallback, finished: IRiotData.OnFinished) {
        CheckUpdate(progress, finished, database, riotApi).run()
    }

    override fun findChampion(championKey: Int, consumer: (Champion) -> Unit) {
        database.findChampion(consumer, championKey)
    }

    override fun findRandomChampion(consumer: (Champion) -> Unit, championKey: Int?, role: String?) {
        database.findRandomChampion(consumer, championKey, role)
    }


    override fun findChampionList(consumer: (Collection<Champion>) -> Unit, role: String?) {
        database.findChampionList(consumer, role)
    }

    override fun isDisposed(): Boolean = disposables.all { it.isDisposed }

    override fun dispose() = disposables.forEach { it.dispose() }

    private class CheckUpdate(private val progress: IProgressCallback, private val finished: IRiotData.OnFinished, private val database: DatabaseManager, private val riotApi: DDragon) : Disposable, Runnable {

        private val disposables = mutableListOf<Disposable>()

        override fun isDisposed(): Boolean = disposables.all { it.isDisposed }

        override fun dispose() = disposables.forEach { it.dispose() }

        override fun run() {
            progress.onProgressUpdate(IProgressCallback.Progress.CHECKING_VERSION)
            disposables += riotApi.updateVersion(this::downloadChampions, this::catchError)
        }

        private fun downloadChampions() {
            progress.onProgressUpdate(IProgressCallback.Progress.PARSING_DATA)
            disposables += riotApi.getChampionList( this::handleChampionList, this::catchError)
        }

        private fun handleChampionList(champions: Collection<Champion>) {
            disposables += database.addChampions(champions)
            val verify = Verify(progress, finished, database, riotApi)
            verify.verifyImages(champions)
            disposables += verify
        }

        private fun catchError(t: Throwable) {
            Log.e(TAG, "catchError: ", t)
            progress.onProgressUpdate(IProgressCallback.Progress.ERROR)
        }

        companion object {
            private val TAG = CheckUpdate::class.java.simpleName
        }
    }

    private class Verify(private val progress: IProgressCallback, private val finished: IRiotData.OnFinished, private val database: DatabaseManager, private val riotApi: DDragon) : Disposable, Runnable {
        private val disposables = mutableListOf<Disposable>()

        override fun isDisposed(): Boolean = disposables.all { it.isDisposed }

        override fun dispose() = disposables.forEach { it.dispose() }

        override fun run() {
            disposables += database.findChampionList(this::verifyImages)
        }

        fun verifyImages(champions: Collection<Champion>) {
            disposables += riotApi.verifyImages(champions, progress, this::downloadMissingOrCorruptImages, this::catchError)
        }

        private fun downloadMissingOrCorruptImages(champions: Collection<ImageDescriptor>) {
            disposables += riotApi.downloadAllImages(champions, progress, finished::onFinished, this::catchError)
        }

        private fun catchError(t: Throwable) {
            Log.e(TAG, "catchError: ", t)
            progress.onProgressUpdate(IProgressCallback.Progress.ERROR)
        }

        companion object {
            private val TAG = CheckUpdate::class.java.simpleName
        }
    }

}