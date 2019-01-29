package com.spiderbiggen.randomchampionselector.data.tasks

import android.util.Log
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import io.reactivex.disposables.Disposable

class UpdateTask(private val progress: IProgressCallback, private val finished: () -> Unit) : Disposable, Runnable {

    private val disposables = mutableListOf<Disposable>()

    override fun isDisposed(): Boolean = disposables.all(Disposable::isDisposed)

    override fun dispose() = disposables.forEach(Disposable::dispose)

    override fun run() {
        progress.onProgressUpdate(IProgressCallback.Progress.CHECKING_VERSION)
        disposables += DDragon.updateVersion(this::downloadChampions, this::catchError)
    }

    private fun downloadChampions() {
        progress.onProgressUpdate(IProgressCallback.Progress.PARSING_DATA)
        disposables += DDragon.getChampionList(this::handleChampionList, this::catchError)
    }

    private fun handleChampionList(champions: Collection<Champion>) {
        disposables += DatabaseManager.addChampions(champions)
        finished()
    }

    private fun catchError(t: Throwable) {
        Log.e(TAG, "catchError: ", t)
        progress.onProgressUpdate(IProgressCallback.Progress.ERROR)
    }

    companion object {
        private val TAG = UpdateTask::class.java.simpleName
    }
}