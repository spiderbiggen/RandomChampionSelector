package com.spiderbiggen.randomchampionselector.presentation.ui.champion.details

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.FragmentChampionDetailsBinding
import com.spiderbiggen.randomchampionselector.presentation.extensions.collectScreenState
import com.spiderbiggen.randomchampionselector.presentation.extensions.viewBindings
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.ChampionViewData
import com.spiderbiggen.randomchampionselector.presentation.ui.common.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class ChampionDetailsFragment : Fragment(R.layout.fragment_champion_details) {

    private val viewBinding by viewBindings(FragmentChampionDetailsBinding::bind)
    private val viewModel by viewModels<ChampionDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        setupView()
        viewModel.handleViewCreated()
    }

    private fun setupView() = with(viewBinding) {
        setupWithNavController(toolbarLayout, toolbar, findNavController())
        fab.setOnClickListener { navigateToDetails() }

        collectScreenState(viewModel.state, ::handleState)
    }

    private fun handleState(state: State<ChampionViewData>) {
        when (state) {
            is State.Loading -> Unit
            is State.Ready -> handleReadyState(state.viewData)
            is State.Error -> Log.e("ChampionsDetailsFragment", "handleState", state.error)
        }
    }


    private fun navigateToDetails() {
        findNavController().navigate(ChampionDetailsFragmentDirections.actionToSelf())
    }


    private fun handleReadyState(viewData: ChampionViewData) = with(viewBinding) {
        championName.text = viewData.title
        championTitle.text = viewData.subtitle
        championBlurb.text = viewData.description
        championSplash.setImageURI(viewData.image)
        startPostponedEnterTransition()
    }
}

