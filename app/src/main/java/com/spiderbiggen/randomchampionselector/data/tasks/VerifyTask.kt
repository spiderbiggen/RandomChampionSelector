package com.spiderbiggen.randomchampionselector.data.tasks

import android.util.Log
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.data.ddragon.ImageDescriptor
import com.spiderbiggen.randomchampionselector.data.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import io.reactivex.disposables.Disposable

class VerifyTask(private val progress: IProgressCallback, private val finished: () -> Unit) : Disposable, Runnable {
    private val disposables = mutableListOf<Disposable>()

    override fun isDisposed(): Boolean = disposables.all(Disposable::isDisposed)

    override fun dispose() = disposables.forEach(Disposable::dispose)

    override fun run() {
        disposables += DatabaseManager.findChampionList(this::verifyImages)
    }

    private fun verifyImages(champions: Collection<Champion>) {
        disposables += DDragon.verifyImages(champions, progress, this::downloadMissingOrCorruptImages, this::catchError)
    }

    private fun downloadMissingOrCorruptImages(champions: Collection<ImageDescriptor>) {
        disposables += DDragon.downloadAllImages(champions, progress, finished, this::catchError)
    }

    private fun catchError(t: Throwable) {
        Log.e(TAG, "catchError: ", t)
        progress.onProgressUpdate(IProgressCallback.Progress.ERROR)
    }

    companion object {
        private val TAG = VerifyTask::class.java.simpleName
    }
}