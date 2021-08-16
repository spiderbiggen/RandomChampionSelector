package com.spiderbiggen.randomchampionselector.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.databinding.ActivityChampionBinding
import com.spiderbiggen.randomchampionselector.util.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.util.data.onMainThread
import com.spiderbiggen.randomchampionselector.models.Champion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException

@ExperimentalCoroutinesApi
class ChampionActivity : AbstractActivity() {

    private lateinit var binding: ActivityChampionBinding

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CHAMPION_KEY, championKey)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        launch(Dispatchers.Default) {
            val champion = when {
                championKey < 0 -> database.findRandomChampion()
                else -> database.findChampion(championKey)
            }
            onMainThread { setChampion(champion) }
        }
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

    private fun setChampion(champion: Champion) {
        launch {
            try {
                val bitmap = BitmapCache.loadBitmap(champion)
                onMainThread { binding.championSplash.setImageBitmap(bitmap) }
            } catch (e: IOException) {
                onMainThread { loadImageFailure(e) }
            }
        }
        championKey = champion.key
        binding.championName.text = champion.name
        binding.championTitle.text = champion.capitalizedTitle
        binding.championBlurb.text = champion.lore
        supportStartPostponedEnterTransition()
    }

    private fun loadImageFailure(e: Throwable) {
        binding.championSplash.setImageBitmap(null)
        Log.e("ChampionActivity", "error ${e.message}", e)
    }

    companion object {
        const val CHAMPION_KEY = "CHAMPION_KEY"
        const val UP_ON_BACK_KEY = "UP_ON_BACK_KEY"
    }
}
