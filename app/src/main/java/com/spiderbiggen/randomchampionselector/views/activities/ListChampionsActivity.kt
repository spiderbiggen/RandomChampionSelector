package com.spiderbiggen.randomchampionselector.views.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.data.onMainThread
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.views.adapters.ChampionAdapter
import kotlinx.android.synthetic.main.activity_list_champions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException

@ExperimentalCoroutinesApi
class ListChampionsActivity : AbstractActivity() {
    private val adapter = ChampionAdapter(mutableListOf(), View.OnClickListener(this::onClick))
    private var champion: Champion? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_champions)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        champion_list.adapter = adapter
    }

    private fun findRandomImage() {
        launch(Dispatchers.Default) {
            val champion = champion ?: dataManager.findRandomChampion()
            try {
                val bitmap: Bitmap = BitmapCache.loadBitmap(champion)
                onMainThread { splash.setImageBitmap(bitmap) }
            } catch (e: IOException) {
                findRandomImage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onResume() {
        launch {
            val list = dataManager.findChampionList()
            onMainThread { adapter.setChampions(list) }
        }
        findRandomImage()
        super.onResume()
    }

    fun openChampion(view: View) {
        openChampionActivity(view)
    }

    private fun onClick(v: View) {
        val position = champion_list.getChildAdapterPosition(v)
        val img = v.findViewById<ImageView>(R.id.champion_splash)
        val name = v.findViewById<TextView>(R.id.champion_name)
        val title = v.findViewById<TextView>(R.id.champion_title)
        selectChampion(position,
                Pair(img, getString(R.string.champion_splash_transition_key)),
                Pair(name, getString(R.string.champion_name_transition_key)),
                Pair(title, getString(R.string.champion_title_transition_key))
        )
    }

    private fun selectChampion(position: Int, vararg views: Pair<View, String>) {
        val champion = adapter.getChampion(position)
        val intent = Intent(this, ChampionActivity::class.java)
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion?.key)
        val options = when (views.size) {
            1 -> ActivityOptionsCompat.makeSceneTransitionAnimation(this, views[0])
            2 -> ActivityOptionsCompat.makeSceneTransitionAnimation(this, views[0], views[1])
            3 -> ActivityOptionsCompat.makeSceneTransitionAnimation(this, views[0], views[1], views[2])
            else -> ActivityOptionsCompat.makeSceneTransitionAnimation(this, *views)
        }
        intent.putExtra(ChampionActivity.UP_ON_BACK_KEY, false)
        startActivityWithFade(intent, options.toBundle())
    }
}
