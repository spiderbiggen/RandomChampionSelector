package com.spiderbiggen.randomchampionselector.presentation.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.databinding.ActivityChampionBinding
import com.spiderbiggen.randomchampionselector.presentation.viewmodels.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class ChampionActivity : AbstractActivity() {

    private lateinit var binding: ActivityChampionBinding

    private val viewModel: ChampionViewModel by viewModels()

    private var championKey = -1
    private var upOnBack: Boolean = false
    private var shouldFinalize = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        championKey = savedInstanceState?.getInt(CHAMPION_KEY) ?: championKey
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey)
        upOnBack = intent.getBooleanExtra(UP_ON_BACK_KEY, true)
        binding = ActivityChampionBinding.inflate(layoutInflater).also { binding ->
            setContentView(binding.root)
            binding.toolbar.also { toolbar ->
                setSupportActionBar(toolbar)
            }
            binding.fab.setOnClickListener { openChampionActivity() }
        }
        supportPostponeEnterTransition()
        supportActionBar?.also { toolbar ->
            toolbar.title = null
            toolbar.setDisplayHomeAsUpEnabled(true)
        }

        viewModel.champion.observe(this, ::setChampion)
        viewModel.bitmap.observe(this, ::setBitmap)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    @Deprecated("Migrate to OnBackPressedDispatcher")
    override fun onBackPressed() {
        if (upOnBack) {
            val upIntent = parentActivityIntent ?: Intent(this, ListChampionsActivity::class.java)
            supportNavigateUpTo(upIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            supportFinishAfterTransition()
        }
    }

    private fun setChampion(champion: Champion?) {
        if (champion == null) return
        championKey = champion.key
        with(binding) {
            championName.text = champion.name
            championTitle.text = champion.capitalizedTitle
            championBlurb.text = champion.lore
        }
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
