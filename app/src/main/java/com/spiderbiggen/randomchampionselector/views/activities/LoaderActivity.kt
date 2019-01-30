package com.spiderbiggen.randomchampionselector.views.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.annotation.UiThread
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import kotlinx.android.synthetic.main.activity_loader.*
import kotlinx.coroutines.*
import java.util.*


@ExperimentalCoroutinesApi
class LoaderActivity : AbstractActivity(), IProgressCallback, CoroutineScope by MainScope() {
    private var shouldRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isNetworkAvailable()){
            openListActivity()
            return
        }
        setContentView(com.spiderbiggen.randomchampionselector.R.layout.activity_loader)
        shouldRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
    }

    override fun onResume() {
        super.onResume()
        when {
            shouldRefresh || dataManager.shouldRefresh -> {
                PreferenceManager.lastSync = -1
                launch(Dispatchers.IO) {
                    dataManager.update(this@LoaderActivity)
                    onFinished()
                }
            }
            else -> launch(Dispatchers.IO) {
                dataManager.verifyImages(this@LoaderActivity)
                onFinished()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
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

    override fun onProgressUpdate(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
        updateProgressBar(type, progress, progressMax)
    }

    @UiThread
    private fun updateProgressBar(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
        if (type === IProgressCallback.Progress.ERROR) {
            val progressDrawable = progressBar.indeterminateDrawable.mutate()
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
            progressBar.progressDrawable = progressDrawable
        }

        progressBar.isIndeterminate = type.indeterminate
        progressBar.progress = progress
        progressBar.max = progressMax

        progressText.text = getString(type.stringResource, progress, progressMax)
    }

}
