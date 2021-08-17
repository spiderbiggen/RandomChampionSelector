package com.spiderbiggen.randomchampionselector.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.UiThread
import com.spiderbiggen.randomchampionselector.DataApplication
import com.spiderbiggen.randomchampionselector.databinding.ActivityLoaderBinding
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback.Progress
import com.spiderbiggen.randomchampionselector.util.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.viewmodels.LoaderViewModel
import com.spiderbiggen.randomchampionselector.viewmodels.LoaderViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Splash Activity that checks for updates and makes sure the images aren't corrupted.
 *
 * @author Stefan Breetveld
 */
class LoaderActivity : AbstractActivity(), IProgressCallback {
    private lateinit var binding: ActivityLoaderBinding
    private var shouldRefresh: Boolean = false

    private val viewModel: LoaderViewModel by viewModels {
        LoaderViewModelFactory((application as DataApplication).championRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNetworkAvailable()) {
            openListActivity()
            return
        }
        binding = ActivityLoaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shouldRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
        viewModel.finished.observe(this, {
            if (it) {
                onFinished()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isNetworkAvailable()) {
            openListActivity()
            return
        }
        when {
            shouldRefresh || PreferenceManager.isOutdated -> {
                PreferenceManager.lastSync = -1
                viewModel.updateData(this)
            }
            else -> viewModel.verifyImages(this@LoaderActivity)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun onFinished() {
        PreferenceManager.lastSync = Date().time
        openListActivity()
    }

    private fun openListActivity() {
        val intent = Intent(this, ListChampionsActivity::class.java)
        startActivity(intent)
    }

    override fun update(type: Progress, progress: Int, progressMax: Int) {
        launch(Dispatchers.Main) {
            val progressBar = binding.progressBar
            if (type === Progress.ERROR) {
                val progressDrawable = progressBar.indeterminateDrawable.mutate()
                progressDrawable.setColorFilter(Color.RED, Mode.SRC_IN)
                progressBar.progressDrawable = progressDrawable
            }

            progressBar.isIndeterminate = type.indeterminate
            progressBar.progress = progress
            progressBar.max = progressMax

            val percent = if (progressMax == 0) 0f else (progress.toFloat() / progressMax) * 100
            binding.progressText.text = getString(type.stringResource, percent)
        }
    }


}
