package com.spiderbiggen.randomchampionselector.presenters

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IChampionInteractor
import com.spiderbiggen.randomchampionselector.views.activities.ChampionActivity
import com.spiderbiggen.randomchampionselector.views.activities.ListChampionsActivity
import com.spiderbiggen.randomchampionselector.views.adapters.ChampionAdapter
import io.reactivex.functions.Consumer

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
class ListChampionsPresenter(context: ListChampionsActivity) : AbstractPresenter<ListChampionsActivity>(context), IChampionInteractor.IChampionListCallback {
    private val adapter = ChampionAdapter(mutableListOf(), View.OnClickListener { context.onClick(it) })

    override fun onCreate(savedInstanceState: Bundle?) {
        context.setAdapter(adapter)
        dataManager.findRandomChampion(Consumer { champion ->
            dataManager.findImageForChampion(Consumer { bitmap -> context.setHeaderImage(bitmap) }, champion)
        })
    }

    override fun onResume() {
        dataManager.findChampionList(this)
    }

    override fun setChampionList(champions: Collection<Champion>) {
        adapter.setChampions(champions.toList())
    }

    fun selectChampion(position: Int, vararg views: kotlin.Pair<View, String>) {
        this.selectChampion(position, *(views.map { Pair(it.first, it.second) }).toTypedArray())
    }

    private fun selectChampion(position: Int, vararg views: Pair<View, String>) {
        val champion = adapter.getChampion(position)
        val intent = Intent(context, ChampionActivity::class.java)
        intent.putExtra(ChampionPresenter.CHAMPION_KEY, champion?.key)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, *views)
        intent.putExtra(ChampionPresenter.UP_ON_BACK_KEY, false)
        startActivityWithFade(intent, options.toBundle())
    }
}