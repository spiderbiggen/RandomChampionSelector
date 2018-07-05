package com.spiderbiggen.randomchampionselector.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback
import android.support.v7.preference.PreferenceScreen
import android.view.MenuItem
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created on 4-7-2018.
 * @author Stefan Breetveld
 */
class SettingsActivity : ButtonActivity(), OnPreferenceStartScreenCallback {

    var needsRefresh = false
        get() {
            val old = field
            field = false
            return old
        }

    private val andResetNeedsRefresh: Boolean
        get() {
            val needsRefresh = this.needsRefresh
            this.needsRefresh = false
            return needsRefresh
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        actionBar?.title = null
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            var fragment: Fragment? = supportFragmentManager.findFragmentByTag(SettingsFragment.FRAGMENT_TAG)
            if (fragment == null) {
                fragment = SettingsFragment()
            }

            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, fragment, SettingsFragment.FRAGMENT_TAG)
            ft.commit()
        }
    }

    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
        val ft = supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        val args = Bundle()
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref?.key)
        fragment.arguments = args
        ft.replace(R.id.fragment_container, fragment, pref?.key)
        ft.addToBackStack(pref?.key)
        ft.commit()
        return true
    }

    override fun onBackPressed() {
        if (startActivityOnRefresh()) {
            return
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!startActivityOnRefresh() && !super.onOptionsItemSelected(item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startActivityOnRefresh(): Boolean {
        if (andResetNeedsRefresh) {
            startActivity(LoaderActivity.createStartIntent(this, true))
            return true
        }
        return false
    }
}