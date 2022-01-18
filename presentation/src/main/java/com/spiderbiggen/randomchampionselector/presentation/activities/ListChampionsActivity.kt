package com.spiderbiggen.randomchampionselector.presentation.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.adapters.ChampionAdapter
import com.spiderbiggen.randomchampionselector.presentation.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.presentation.databinding.ActivityListChampionsBinding
import com.spiderbiggen.randomchampionselector.presentation.databinding.ItemChampionBinding
import com.spiderbiggen.randomchampionselector.presentation.viewmodels.ChampionListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListChampionsActivity : AbstractActivity() {
    private lateinit var binding: ActivityListChampionsBinding

    @Inject
    internal lateinit var bitmapCache: BitmapCache
    private lateinit var adapter: ChampionAdapter

    private val viewModel: ChampionListViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChampionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title

        adapter = ChampionAdapter(View.OnClickListener(this::onClick), bitmapCache)

        binding.championList.adapter = adapter
        binding.fab.setOnClickListener { openChampionActivity() }
        viewModel.champions.observe(this, {
            viewModel.selectRandomChampion()
            adapter.setChampions(it)
        })
        viewModel.bitmap.observe(this, { binding.splash.setImageBitmap(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun onClick(v: View) {
        val position = binding.championList.getChildAdapterPosition(v)
        val itemBinding = ItemChampionBinding.bind(v)
        val img = itemBinding.championSplash
        val name = itemBinding.championName
        val title = itemBinding.championTitle
        val champion = adapter.getChampion(position)
        val intent = Intent(this, ChampionActivity::class.java)
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion?.key)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair(img, getString(R.string.champion_splash_transition_key)),
            Pair(name, getString(R.string.champion_name_transition_key)),
            Pair(title, getString(R.string.champion_title_transition_key))
        )
        intent.putExtra(ChampionActivity.UP_ON_BACK_KEY, false)
        startActivityWithFade(intent, options.toBundle())
    }
}
