package com.spiderbiggen.randomchampionselector.views.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.presenters.ChampionPresenter
import kotlinx.android.synthetic.main.activity_champion.*

class ChampionActivity : AppCompatActivity() {
    private val presenter = ChampionPresenter(this)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champion)
        setSupportActionBar(toolbar)
        supportPostponeEnterTransition()
        val actionBar = supportActionBar
        actionBar?.title = null
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        presenter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            presenter.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    override fun onBackPressed() {
        presenter.onBackPressed()
    }


    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()

    }

    fun openChampion(view: View) {
        presenter.openChampionActivity(view)
    }

    fun setChampion(champion: Champion) {
        champion_name.text = champion.name
        champion_title.text = champion.capitalizedTitle
        champion_blurb.text = champion.lore
        supportStartPostponedEnterTransition()
    }

    fun loadImageSuccess(bitmap: Bitmap) {
        champion_splash.setImageBitmap(bitmap)
    }

    fun loadImageFailure(message: String?) {
        champion_splash.setImageBitmap(null)
        Log.e("ChampionActivity", "error $message")
    }
}
