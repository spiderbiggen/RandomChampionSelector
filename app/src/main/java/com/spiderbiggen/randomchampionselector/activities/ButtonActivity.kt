package com.spiderbiggen.randomchampionselector.activities

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.spiderbiggen.randomchampionselector.R

/**
 * Fullscreen activity Created by Stefan on 9-5-2015.
 */
abstract class ButtonActivity : AppCompatActivity() {

    protected val championIntent: Intent
        get() = Intent(this, ChampionActivity::class.java)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    supportFinishAfterTransition()
                    true
                }
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.action_force_refresh -> {
                    startActivity(LoaderActivity.createStartIntent(this, true))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    open fun openChampion(view: View) {
        startActivity(championIntent)
    }

    override fun startActivity(intent: Intent) {
        val options = ActivityOptionsCompat
                .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        super.startActivity(intent, options.toBundle())
    }
}
