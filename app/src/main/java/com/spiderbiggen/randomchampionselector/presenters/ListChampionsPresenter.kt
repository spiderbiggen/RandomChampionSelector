package com.spiderbiggen.randomchampionselector.presenters

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.widget.ImageView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IChampionInteractor
import com.spiderbiggen.randomchampionselector.views.activities.ChampionActivity
import com.spiderbiggen.randomchampionselector.views.activities.ListChampionsActivity
import com.spiderbiggen.randomchampionselector.views.adapters.ChampionAdapter
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
class ListChampionsPresenter(context: ListChampionsActivity) : AbstractPresenter<ListChampionsActivity>(context), IChampionInteractor.IChampionListCallback {
    private val adapter = ChampionAdapter(mutableListOf(), View.OnClickListener { context.onClick(it) })

    override fun onCreate(savedInstanceState: Bundle?) {
        context.setAdapter(adapter)
    }

    override fun onResume() {
        dataManager.findChampionList(this)
    }

    override fun setChampionList(champions: Collection<Champion>) {
        adapter.setChampions(champions.toList())
    }


    fun selectChampion(position: Int, img: ImageView) {
        val champion = adapter.getChampion(position)
        val intent = Intent(context, ChampionActivity::class.java)
        intent.putExtra(ChampionPresenter.CHAMPION_KEY, champion?.key)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, img, getString(R.string.champion_splash_transition_key))
        intent.putExtra(ChampionPresenter.UP_ON_BACK_KEY, false)
        startActivityWithFade(intent, options.toBundle())
    }
}