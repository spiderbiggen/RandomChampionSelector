package com.spiderbiggen.randomchampionselector.presenters

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.spiderbiggen.randomchampionselector.data.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.model.IChampionInteractor
import com.spiderbiggen.randomchampionselector.views.activities.ChampionActivity
import com.spiderbiggen.randomchampionselector.views.activities.ListChampionsActivity
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
class ChampionPresenter(context: ChampionActivity) : AbstractPresenter<ChampionActivity>(context), IChampionInteractor.IChampionCallBack {

    private var championKey = -1
    private var upOnBack: Boolean = false
    private var shouldFinalize = false

    override fun onCreate(savedInstanceState: Bundle?) {
        championKey = savedInstanceState?.getInt(CHAMPION_KEY) ?: championKey

        val intent = context.intent
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey)
        upOnBack = intent.getBooleanExtra(UP_ON_BACK_KEY, true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CHAMPION_KEY, championKey)
    }

    override fun onResume() {
        when {
            championKey < 0 -> dataManager.findRandomChampion(this, championKey)
            else -> dataManager.findChampion(this, championKey)
        }
    }

    override fun onStop() {
        if (shouldFinalize) {
            context.finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun setChampion(champion: Champion) {
        championKey = champion.key
        context.setChampion(champion)
        DDragon.getChampionBitmapFromCache(champion, context)
    }

    fun onBackPressed() {
        if (upOnBack) {
            val upIntent = context.parentActivityIntent
                    ?: Intent(context, ListChampionsActivity::class.java)
            context.supportNavigateUpTo(upIntent)
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            context.supportFinishAfterTransition()
        }
    }

    companion object {
        const val CHAMPION_KEY = "CHAMPION_KEY"
        const val UP_ON_BACK_KEY = "UP_ON_BACK_KEY"
    }

}