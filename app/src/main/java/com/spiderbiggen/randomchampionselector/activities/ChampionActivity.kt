package com.spiderbiggen.randomchampionselector.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.spiderbiggen.randomchampionselector.DataApplication
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.databinding.ActivityChampionBinding
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.viewmodels.ChampionViewModel
import com.spiderbiggen.randomchampionselector.viewmodels.ChampionViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChampionActivity : AbstractActivity() {

    private lateinit var binding: ActivityChampionBinding

    private val viewModel: ChampionViewModel by viewModels {
        ChampionViewModelFactory((application as DataApplication).championRepository)
    }

    private var championKey = -1
    private var upOnBack: Boolean = false
    private var shouldFinalize = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        championKey = savedInstanceState?.getInt(CHAMPION_KEY) ?: championKey
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey)
        upOnBack = intent.getBooleanExtra(UP_ON_BACK_KEY, true)
        binding = ActivityChampionBinding.inflate(layoutInflater)
        binding.activity = this
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportPostponeEnterTransition()
        val actionBar = supportActionBar
        actionBar?.title = null
        actionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.champion.observe(this, { setChampion(it) })
        viewModel.bitmap.observe(this, { setBitmap(it) })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CHAMPION_KEY, championKey)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        viewModel.setChampion(championKey)
        super.onResume()
    }

    override fun onStop() {
        if (shouldFinalize) {
            finish()
        }
        super.onStop()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBackPressed() {
        if (upOnBack) {
            val upIntent = parentActivityIntent
                ?: Intent(this, ListChampionsActivity::class.java)
            supportNavigateUpTo(upIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            supportFinishAfterTransition()
        }
    }

    fun openChampion(view: View) {
        openChampionActivity(view)
    }

    private fun setChampion(champion: Champion?) {
        if (champion == null) return
        championKey = champion.key
        binding.championName.text = champion.name
        binding.championTitle.text = champion.capitalizedTitle
        binding.championBlurb.text = champion.lore
        supportStartPostponedEnterTransition()
    }

    private fun setBitmap(bitmap: Bitmap?) {
        binding.championSplash.setImageBitmap(bitmap)
    }

        companion object {
        const val CHAMPION_KEY = "CHAMPION_KEY"
        const val UP_ON_BACK_KEY = "UP_ON_BACK_KEY"
    }
}
