package com.spiderbiggen.randomchampionselector.presentation.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.FragmentSplashBinding
import com.spiderbiggen.randomchampionselector.presentation.extensions.getColorIntFromAttr
import com.spiderbiggen.randomchampionselector.presentation.extensions.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

/**
 * Splash Activity that checks for updates and makes sure the images aren't corrupted.
 *
 * @author Stefan Breetveld
 */
@FlowPreview
@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel by viewModels<SplashViewModel>()
    private val viewBinding by viewBindings(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { onState(it) }
        viewModel.handleViewCreated()
    }

    private fun onState(state: DownloadProgress) = with(viewBinding) {
        var count = 0
        var progressMax = 0

        with(progressBar) {
            @StringRes
            var text = -1
            when (state) {
                DownloadProgress.Idle -> {
                    isVisible = false
                    return
                }
                DownloadProgress.NoInternet -> navigateToList()
                is DownloadProgress.Error -> {
                    isVisible = true
                    root.postDelayed(2500L) {
                        navigateToList()
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
                    navigateToList()
                    return
                }
            }

            if (state is DownloadProgress.Error) {
                progressDrawable = indeterminateDrawable.mutate().apply {
                    val errorColor = requireContext().getColorIntFromAttr(R.attr.colorError)
                    setTint(errorColor)
                }
            }

            isIndeterminate = state.indeterminate
            progress = count
            max = progressMax
            if (text != -1 && isVisible) {
                val percent = if (progressMax == 0) 0f else (count.toFloat() / progressMax) * 100
                progressText.text = getString(text, percent)
            }
        }
    }

    private fun navigateToList() {
        findNavController().navigate(SplashFragmentDirections.actionToChampionOverview())
    }
}
