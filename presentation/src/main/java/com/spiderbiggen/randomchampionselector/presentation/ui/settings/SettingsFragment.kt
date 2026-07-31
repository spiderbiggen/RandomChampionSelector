package com.spiderbiggen.randomchampionselector.presentation.ui.settings

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.FragmentSettingsBinding
import com.spiderbiggen.randomchampionselector.presentation.extensions.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * Hosts the toolbar for the settings screen, the preferences themselves live in
 * [SettingsPreferenceFragment].
 *
 * Preference changes are collected in [SettingsViewModel] while the user is on this screen. Leaving
 * the screen sends them through the splash screen, which clears the cached images and re-syncs.
 *
 * @author Stefan Breetveld
 */
@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewBinding by viewBindings(FragmentSettingsBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The title comes from the destination label in the navigation graph.
        viewBinding.toolbar.setupWithNavController(
            findNavController(),
            AppBarConfiguration.Builder(R.id.championsOverviewFragment).build(),
        )
        // setupWithNavController installed its own up handler, replace it so the up arrow leaves
        // the screen the same way the back button does.
        viewBinding.toolbar.setNavigationOnClickListener { leaveSettings() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = leaveSettings()
            },
        )
    }

    private fun leaveSettings() {
        val navController = findNavController()
        if (viewModel.syncRequired) {
            navController.navigate(
                SettingsFragmentDirections.actionGlobalSplashFragment(
                    refreshChampionData = viewModel.refreshChampionData,
                    clearImages = viewModel.clearImages,
                ),
            )
        } else if (!navController.popBackStack()) {
            requireActivity().finish()
        }
    }
}
