package com.spiderbiggen.randomchampionselector.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.ddragon.ImageDescriptor
import com.spiderbiggen.randomchampionselector.model.Champion
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_loader.*
import java.util.*

class LoaderActivity : AppCompatActivity(), ProgressCallback {

    private val disposables = ArrayList<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStorage()
        initApis()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        var shouldRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
        if (!shouldRefresh) {
            val syncTime = Integer.parseInt(preferences.getString(getString(R.string.pref_title_sync_frequency), getString(R.string.pref_sync_frequency_default)))
            if (syncTime != -1) {
                val lastSync = preferences.getLong(getString(R.string.pref_last_sync_key), DEFAULT_TIME_MILLIS.toLong())
                val nextSync = Date(lastSync + syncTime * MILLIS_IN_MINUTE)
                val now = Date()
                shouldRefresh = lastSync == DEFAULT_TIME_MILLIS.toLong() || now.after(nextSync)
            }
        }
        if (shouldRefresh) {
            setContentView(R.layout.activity_loader)
            startLoading()
        } else {
            openMainScreen()
        }
    }

    private fun initStorage() {
        FileStorage.useContext(this)
        DatabaseManager.useContext(this)
    }

    private fun initApis() {
        DDragon.useContext(this)
    }

    override fun onDestroy() {
        disposables.forEach { it.dispose() }
        super.onDestroy()
    }

    private fun startLoading() {
        disposables += DDragon.updateVersion(Action { this.downloadChampions() }, Consumer { this.catchError(it) })
    }

    private fun downloadChampions() {
        disposables.add(DDragon.getChampionList(Consumer { this.handleChampionList(it) }, Consumer { this.catchError(it) }))
    }

    private fun handleChampionList(champions: List<Champion>) {
        disposables.add(DatabaseManager.addChampions(champions))
        disposables.add(DDragon.verifyImages(champions, this, Consumer { this.downloadMissingOrCorruptImages(it) }, Consumer { this.catchError(it) }))
    }

    private fun catchError(t: Throwable) {
        Log.e(TAG, "catchError: ", t)
        onProgressUpdate(ProgressCallback.Progress.ERROR)
    }

    private fun downloadMissingOrCorruptImages(champions: List<ImageDescriptor>) {
        disposables.add(DDragon.downloadAllImages(champions, this, Action { this.openMainScreen() }, Consumer { this.catchError(it) }))
    }

    private fun openMainScreen() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(getString(R.string.pref_last_sync_key), Date().time)
                .apply()
        val intent = Intent(this, ListChampionsActivity::class.java)
        startActivity(intent)
    }

    override fun onProgressUpdate(type: ProgressCallback.Progress, progress: Int, progressMax: Int) {
        val progressBar = this.progressBar

        if (type === ProgressCallback.Progress.ERROR) {
            val progressDrawable = progressBar.indeterminateDrawable.mutate()
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
            progressBar.progressDrawable = progressDrawable
        }

        progressBar.isIndeterminate = type.indeterminate
        progressBar.progress = progress
        progressBar.max = progressMax

        progressText.text = getString(type.stringResource, progress, progressMax)
    }

    override fun finishExecution() = Unit

    companion object {

        const val FORCE_REFRESH = "FORCE_REFRESH"
        const val DEFAULT_TIME_MILLIS = -1
        const val MILLIS_IN_MINUTE = 60_000

        private val TAG = LoaderActivity::class.java.simpleName

        fun createStartIntent(context: Context, forceRefresh: Boolean): Intent {
            val intent = Intent(context, LoaderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(LoaderActivity.FORCE_REFRESH, forceRefresh)
            return intent
        }
    }

}
