package com.spiderbiggen.randomchampionselector.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.spiderbiggen.randomchampionselector.DataApplication
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.adapters.ChampionAdapter
import com.spiderbiggen.randomchampionselector.databinding.ActivityListChampionsBinding
import com.spiderbiggen.randomchampionselector.viewmodels.ChampionListViewModel
import com.spiderbiggen.randomchampionselector.viewmodels.ChampionListViewModelFactory
import kotlinx.coroutines.*

class ListChampionsActivity : AbstractActivity() {
    private lateinit var binding: ActivityListChampionsBinding

    private val adapter = ChampionAdapter(View.OnClickListener(this::onClick))

    private val viewModel: ChampionListViewModel by viewModels {
        ChampionListViewModelFactory((application as DataApplication).championRepository)
    }

    @ExperimentalCoroutinesApi
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChampionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
        binding.championList.adapter = adapter
        binding.activity = this
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

    fun openChampion(view: View) {
        openChampionActivity(view)
    }

    private fun onClick(v: View) {
        val position = binding.championList.getChildAdapterPosition(v)
        val img = v.findViewById<ImageView>(R.id.champion_splash)
        val name = v.findViewById<TextView>(R.id.champion_name)
        val title = v.findViewById<TextView>(R.id.champion_title)
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
