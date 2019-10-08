package com.spiderbiggen.randomchampionselector.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback.Progress
import com.spiderbiggen.randomchampionselector.util.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.util.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.util.data.onMainThread
import kotlinx.android.synthetic.main.activity_loader.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

/**
 * Splash Activity that checks for updates and makes sure the images aren't corrupted.
 *
 * @author Stefan Breetveld
 */
@ExperimentalCoroutinesApi
class LoaderActivity : AbstractActivity(), IProgressCallback {
    private var shouldRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNetworkAvailable()) {
            openListActivity()
            return
        }
        setContentView(R.layout.activity_loader)
        shouldRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
    }

    override fun onResume() {
        super.onResume()

        when {
            shouldRefresh || PreferenceManager.isOutdated -> {
                PreferenceManager.lastSync = -1
                Log.d("LoaderActivity", "aaaaaaaa")
                updateData(this)
            }
            else -> {
                verifyImages(this@LoaderActivity)
                onFinished()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }

    private fun onFinished() {
        PreferenceManager.lastSync = Date().time
        openListActivity()
    }

    private fun openListActivity() {
        val intent = Intent(this, ListChampionsActivity::class.java)
        startActivity(intent)
    }

    @UiThread
    override fun update(type: Progress, progress: Int, progressMax: Int) {
        if (type === Progress.ERROR) {
            val progressDrawable = progressBar.indeterminateDrawable.mutate()
            progressDrawable.setColorFilter(Color.RED, Mode.SRC_IN)
            progressBar.progressDrawable = progressDrawable
        }

        progressBar.isIndeterminate = type.indeterminate
        progressBar.progress = progress
        progressBar.max = progressMax

        val percent = if (progressMax == 0) 0f else (progress.toFloat() / progressMax) * 100
        progressText.text = getString(type.stringResource, percent)
    }

    private fun verifyImages(progress: IProgressCallback) {
        launch(Dispatchers.IO) {
            val champions = database.findChampionList()
            Log.d("LoaderActivity", champions.toString())
            if (champions.isNullOrEmpty()) {
                updateData(progress)
            } else {
                val things = DDragon.verifyImages(champions, progress)
                Log.d("LoaderActivity", things.toString())
                DDragon.downloadAllImages(things, progress)
            }
        }
    }

    private fun updateData(progress: IProgressCallback) {
        launch(Dispatchers.IO) {
            progress.update(Progress.CHECKING_VERSION)
            val version = DDragon.getLastVersion()
            val champions = DDragon.getChampionList(version)
            Log.d("LoaderActivity", champions.toString())
            database.addChampions(champions)
            val things = DDragon.verifyImages(champions, progress)
            Log.d("LoaderActivity", things.toString())
            if (!things.isEmpty()) {
                DDragon.downloadAllImages(things, progress)
            }
            onMainThread {
                onFinished()
            }
        }
    }

}
