package com.spiderbiggen.randomchampionselector.views.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.domain.Champion
import kotlinx.android.synthetic.main.activity_champion.*

class ChampionActivity : AbstractActivity() {

    private var championKey = -1
    private var upOnBack: Boolean = false
    private var shouldFinalize = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        championKey = savedInstanceState?.getInt(CHAMPION_KEY) ?: championKey
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey)
        upOnBack = intent.getBooleanExtra(UP_ON_BACK_KEY, true)
        setContentView(R.layout.activity_champion)
        setSupportActionBar(toolbar)
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
        when {
            championKey < 0 -> dataManager.findRandomChampion(this::setChampion, championKey)
            else -> dataManager.findChampion(championKey, this::setChampion)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
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
        championKey = champion.key
        BitmapCache.loadBitmap(champion, ::loadImageSuccess, ::loadImageFailure)
        champion_name.text = champion.name
        champion_title.text = champion.capitalizedTitle
        champion_blurb.text = champion.lore
        supportStartPostponedEnterTransition()
    }

    private fun loadImageSuccess(bitmap: Bitmap) {
        champion_splash.setImageBitmap(bitmap)
    }

    private fun loadImageFailure(message: String?) {
        champion_splash.setImageBitmap(null)
        Log.e("ChampionActivity", "error $message")
    }

    companion object {
        const val CHAMPION_KEY = "CHAMPION_KEY"
        const val UP_ON_BACK_KEY = "UP_ON_BACK_KEY"
    }
}
