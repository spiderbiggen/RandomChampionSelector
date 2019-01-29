package com.spiderbiggen.randomchampionselector.views.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback
import androidx.preference.PreferenceScreen
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.views.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created on 4-7-2018.
 * @author Stefan Breetveld
 */
class SettingsActivity : AbstractActivity(), OnPreferenceStartScreenCallback {

    private var needsRefresh = false
        get() {
            val old = field
            field = false
            return old
        }

    private fun startActivityOnRefresh(): Boolean {
        if (needsRefresh) {
            startActivity(createStartIntent())
            return true
        }
        return false
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            val fragment: Fragment = supportFragmentManager.findFragmentByTag(SettingsFragment.FRAGMENT_TAG)
                    ?: SettingsFragment()

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment, SettingsFragment.FRAGMENT_TAG)
            transaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        val args = Bundle()
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref?.key)
        fragment.arguments = args
        transaction.replace(R.id.fragment_container, fragment, pref?.key)
        transaction.addToBackStack(pref?.key)
        transaction.commit()
        return true
    }

    override fun onBackPressed() {
        if (!startActivityOnRefresh())
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            if (!startActivityOnRefresh() && !super.onOptionsItemSelected(item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun shouldRefresh() {
        needsRefresh = true
    }
}