package com.spiderbiggen.randomchampionselector.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.model.Champion
import com.spiderbiggen.randomchampionselector.model.ImageType
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_champion.*

class ChampionActivity : ButtonActivity() {

    private var championKey = -1
    private var upOnBack: Boolean = false
    private var disposable: Disposable? = null
    private var shouldFinalize = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champion)
        setSupportActionBar(toolbar)
        supportPostponeEnterTransition()
        val actionBar = supportActionBar
        actionBar?.title = null
        actionBar?.setDisplayHomeAsUpEnabled(true)

        championKey = savedInstanceState?.getInt(CHAMPION_KEY) ?: championKey

        val intent = intent
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey)
        upOnBack = intent.getBooleanExtra(UP_ON_BACK_KEY, true)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CHAMPION_KEY, championKey)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        disposable = when {
            championKey < 0 -> DatabaseManager.findRandomChampion(Consumer { this.setChampion(it) }, championKey)
            else -> DatabaseManager.findChampion(Consumer { this.setChampion(it) }, championKey)
        }
        super.onResume()
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
            val upIntent = parentActivityIntent ?: Intent(this, ListChampionsActivity::class.java)
            supportNavigateUpTo(upIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            supportFinishAfterTransition()
        }
    }

    override fun onPause() {
        disposable?.dispose()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (shouldFinalize) {
            finish()
        }
    }

    override fun openChampion(view: View) {
        super.openChampion(view)
        shouldFinalize = true
    }

    private fun setChampion(champion: Champion?) {
        if (champion == null) {
            championKey = -1
            return
        }
        championKey = champion.key
        DDragon.getChampionBitmapFromCache(champion, imageType, champion_splash)
//        val bitmap: Bitmap? = try {
//            DDragon.getChampionBitmap(champion, imageType)
//        } catch (e: IOException) {
//            null
//        }
//
//        champion_splash?.setImageBitmap(bitmap)
        champion_name.text = champion.name
        champion_title.text = champion.capitalizedTitle
        champion_blurb.text = champion.lore
        supportStartPostponedEnterTransition()
    }

    companion object {
        const val CHAMPION_KEY = "CHAMPION_KEY"
        const val UP_ON_BACK_KEY = "UP_ON_BACK_KEY"
        private val TAG = ChampionActivity::class.java.simpleName
        private val imageType = ImageType.SPLASH
    }
}
