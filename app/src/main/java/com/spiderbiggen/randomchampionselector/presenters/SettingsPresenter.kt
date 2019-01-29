package com.spiderbiggen.randomchampionselector.presenters

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.app.NavUtils
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.views.activities.SettingsActivity
import com.spiderbiggen.randomchampionselector.views.fragments.SettingsFragment

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
class SettingsPresenter(context: SettingsActivity, @IdRes private val fragmentContainer: Int) : AbstractPresenter<SettingsActivity>(context) {

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

    fun startActivityOnRefresh(): Boolean {
        if (andResetNeedsRefresh) {
            startActivity(LoaderPresenter.createStartIntent(context, true))
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            val fragment: Fragment = context.supportFragmentManager.findFragmentByTag(SettingsFragment.FRAGMENT_TAG)
                    ?: SettingsFragment()

            val transaction = context.supportFragmentManager.beginTransaction()
            transaction.replace(fragmentContainer, fragment, SettingsFragment.FRAGMENT_TAG)
            transaction.commit()
        }
    }

    fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
        val transaction = context.supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        val args = Bundle()
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref?.key)
        fragment.arguments = args
        transaction.replace(R.id.fragment_container, fragment, pref?.key)
        transaction.addToBackStack(pref?.key)
        transaction.commit()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                android.R.id.home -> {
                    if (!startActivityOnRefresh() && !super.onOptionsItemSelected(item)) {
                        NavUtils.navigateUpFromSameTask(context)
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}