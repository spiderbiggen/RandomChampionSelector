package com.spiderbiggen.randomchampionselector.views.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.presenters.ListChampionsPresenter
import com.spiderbiggen.randomchampionselector.views.adapters.ChampionAdapter
import kotlinx.android.synthetic.main.activity_list_champions.*

class ListChampionsActivity : AppCompatActivity() {

    private val presenter = ListChampionsPresenter(this)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_champions)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        presenter.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            presenter.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    fun setHeaderImage(bitmap: Bitmap) {
        splash.setImageBitmap(bitmap)
    }

    fun setAdapter(adapter: ChampionAdapter) {
        champion_list.adapter = adapter
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    fun openChampion(view: View) {
        presenter.openChampionActivity(view)
    }

    fun onClick(v: View) {
        val position = champion_list.getChildAdapterPosition(v)
        val img = v.findViewById<ImageView>(R.id.champion_splash)
        val name = v.findViewById<TextView>(R.id.champion_name)
        val title = v.findViewById<TextView>(R.id.champion_title)
        presenter.selectChampion(position,
                Pair(img, getString(R.string.champion_splash_transition_key)),
                Pair(name, getString(R.string.champion_name_transition_key)),
                Pair(title, getString(R.string.champion_title_transition_key))
        )
    }
}
