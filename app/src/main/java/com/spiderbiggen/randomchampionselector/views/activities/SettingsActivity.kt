package com.spiderbiggen.randomchampionselector.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback
import androidx.preference.PreferenceScreen
import android.view.Menu
import android.view.MenuItem
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.presenters.SettingsPresenter
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created on 4-7-2018.
 * @author Stefan Breetveld
 */
class SettingsActivity : AppCompatActivity(), OnPreferenceStartScreenCallback {

    private val presenter = SettingsPresenter(this, R.id.fragment_container)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean =
            presenter.onPreferenceStartScreen(caller, pref)

    override fun onBackPressed() {
        if (!presenter.startActivityOnRefresh())
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            presenter.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    fun shouldRefresh() {
        presenter.needsRefresh = true
    }
}