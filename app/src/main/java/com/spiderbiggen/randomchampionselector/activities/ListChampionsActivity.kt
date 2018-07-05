package com.spiderbiggen.randomchampionselector.activities

import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.widget.ImageView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager
import com.spiderbiggen.randomchampionselector.view.adapters.ChampionAdapter
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_list_champions.*
import java.util.*

class ListChampionsActivity : ButtonActivity() {

    private val adapter: ChampionAdapter
    private var disposable: Disposable? = null

    init {
        adapter = ChampionAdapter(ArrayList(), View.OnClickListener { this.onClick(it) })
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_champions)
        setSupportActionBar(toolbar)
        champion_list.adapter = adapter
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = title
        }
    }

    override fun onResume() {
        disposable = DatabaseManager.findChampionList(Consumer { adapter.setChampions(it) }, null)
        super.onResume()
    }

    override fun onPause() {
        disposable?.dispose()
        super.onPause()
    }

    private fun onClick(v: View) {
        val position = champion_list.getChildAdapterPosition(v)
        val champion = adapter.getChampion(position)
        val img = v.findViewById<ImageView>(R.id.champion_splash)

        val intent = championIntent
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion?.key)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, img, getString(R.string.champion_splash_transition_key))
        intent.putExtra(ChampionActivity.UP_ON_BACK_KEY, false)
        startActivity(intent, options.toBundle())
    }
}
