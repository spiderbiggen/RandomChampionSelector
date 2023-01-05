package com.spiderbiggen.randomchampionselector.presentation.ui.champion.overview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.FragmentChampionsOverviewBinding
import com.spiderbiggen.randomchampionselector.presentation.extensions.collectScreenState
import com.spiderbiggen.randomchampionselector.presentation.extensions.removeAdapterOnDestroy
import com.spiderbiggen.randomchampionselector.presentation.extensions.viewBindings
import com.spiderbiggen.randomchampionselector.presentation.ui.common.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@FlowPreview
@AndroidEntryPoint
class ChampionsOverviewFragment : Fragment(R.layout.fragment_champions_overview) {

    private val viewBinding by viewBindings(FragmentChampionsOverviewBinding::bind)
    private val viewModel by viewModels<ChampionsOverviewViewModel>()

    private val glideRequests by lazy { Glide.with(this@ChampionsOverviewFragment) }
    private val fullRequest by lazy {
        glideRequests
            .asDrawable()
            .centerCrop()
            .placeholder(ColorDrawable(Color.GRAY));
    }

    private val thumbRequest by lazy {
        glideRequests
            .asDrawable()
            .override(121, 72)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(withCrossFade())
    }
    private val listAdapter by lazy {
        ChampionAdapter(::navigateToDetails, fullRequest, thumbRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        viewModel.handleViewCreated()
    }

    private fun setupView() = with(viewBinding) {
        toolbar.apply {
            inflateMenu(R.menu.menu)
            toolbar.setOnMenuItemClickListener(::onOptionItemSelected)
            setupWithNavController(toolbarLayout, toolbar, findNavController(), AppBarConfiguration.Builder(R.id.championsOverviewFragment).build())
        }
        championList.apply {
            val sizeProvider = ViewPreloadSizeProvider<Uri>()
            val preloader = RecyclerViewPreloader(Glide.with(this@ChampionsOverviewFragment), listAdapter, sizeProvider, 10)
            adapter = listAdapter
            addOnScrollListener(preloader)

            addRecyclerListener { holder ->
                val vh = holder as? ChampionAdapter.ViewHolder
                vh?.binding?.championSplash?.let { glideRequests.clear(it) }
            }
            removeAdapterOnDestroy(viewLifecycleOwner)
        }
        fab.setOnClickListener { navigateToDetails() }
        collectScreenState(viewModel.state, ::handleState)
    }

    private fun handleState(state: State<ChampionsOverviewViewData>) {
        when (state) {
            is State.Error -> Unit
            is State.Loading -> Unit
            is State.Ready -> handleReadyState(state.viewData)
        }
    }

    private fun handleReadyState(viewData: ChampionsOverviewViewData) {
        Glide.with(this)
            .load(viewData.headerImage)
            .into(viewBinding.splash)
        listAdapter.setChampions(viewData.items)
    }

    private fun onOptionItemSelected(item: MenuItem): Boolean {
        println("Selected menu item ${item.itemId} ${item.title}")

        return when (item.itemId) {
            R.id.action_settings -> {
                true
            }

            R.id.action_force_refresh -> {
                findNavController().navigate(ChampionsOverviewFragmentDirections.actionGlobalSplashFragment(forceRefresh = true))
                true
            }

            R.id.action_refresh_images -> {
                findNavController().navigate(ChampionsOverviewFragmentDirections.actionGlobalSplashFragment(clearImages = true))
                true
            }

            else -> false
        }
    }

    private fun navigateToDetails(key: Int? = null) {
        val directions: NavDirections = ChampionsOverviewFragmentDirections.actionToChampionDetails(key ?: -1)
        findNavController().navigate(directions)
    }

    private companion object {
        private const val SQUARE_THUMB_SIZE = 200
    }
}

