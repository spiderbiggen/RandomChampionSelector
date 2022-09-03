package com.spiderbiggen.randomchampionselector.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.ActivityLoaderBinding
import com.spiderbiggen.randomchampionselector.presentation.extensions.getColorIntFromAttr
import com.spiderbiggen.randomchampionselector.presentation.viewmodels.LoaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

/**
 * Splash Activity that checks for updates and makes sure the images aren't corrupted.
 *
 * @author Stefan Breetveld
 */
@FlowPreview
@AndroidEntryPoint
class LoaderActivity : AbstractActivity() {
    private val viewModel: LoaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoaderBinding.inflate(layoutInflater)
        val forceRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
        setContentView(binding.root)
        viewModel.state.observe(this) { onState(binding, it) }
        viewModel.loadData(forceRefresh)
    }

    private fun onState(binding: ActivityLoaderBinding, state: DownloadProgress) = with(binding.progressBar) {
        var count = 0
        var progressMax = 0

        @StringRes
        var text = -1
        when (state) {
            DownloadProgress.Idle -> {
                isVisible = false
                return@with
            }
            DownloadProgress.NoInternet -> openListActivity()
            is DownloadProgress.Error -> {
                isVisible = true
                binding.root.postDelayed(2500L) {
                    openListActivity()
                }
                Log.e("LoaderActivity", state.t.message, state.t)
                text = R.string.progress_error
            }
            DownloadProgress.CheckingVersion -> {
                isVisible = true
                R.string.checking_version
            }
            DownloadProgress.UpdateChampions -> text = R.string.parsing_data
            is DownloadProgress.Unvalidated -> Unit
            is DownloadProgress.Validating -> {
                text = R.string.progress_verified
                count = state.completed
                progressMax = state.total
            }
            is DownloadProgress.Downloaded -> {
                isVisible = true
                text = R.string.progress_downloads
                count = state.completed
                progressMax = state.total
            }
            DownloadProgress.DownloadedSuccess -> Unit
            is DownloadProgress.Success -> {
                openListActivity()
                return@with
            }
        }

        if (state is DownloadProgress.Error) {
            progressDrawable = indeterminateDrawable.mutate().apply {
                val errorColor = getColorIntFromAttr(R.attr.colorError)
                setTint(errorColor)
            }
        }

        isIndeterminate = state.indeterminate
        progress = count
        max = progressMax
        if (text != -1 && isVisible) {
            val percent = if (progressMax == 0) 0f else (count.toFloat() / progressMax) * 100
            binding.progressText.text = getString(text, percent)
        }
    }

    private fun openListActivity() {
        val intent = Intent(this, ListChampionsActivity::class.java)
        startActivity(intent)
    }
}
