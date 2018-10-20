package com.spiderbiggen.randomchampionselector.presenters

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.*
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.DataManager
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.views.activities.ChampionActivity
import com.spiderbiggen.randomchampionselector.views.activities.SettingsActivity
import java.lang.ref.WeakReference

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
abstract class AbstractPresenter<T : AppCompatActivity>(weakContext: T) {

    private val weakContext = WeakReference<T>(weakContext)
    protected val context: T
        get() = weakContext.get()!!
    protected val dataManager = DataManager(context)

    open fun onCreate(savedInstanceState: Bundle? = null) {}

    open fun onDestroy() {
        dataManager.dispose()
    }

    open fun onResume() {}

    fun onPause() {
        dataManager.dispose()
    }

    open fun onStop() {}

    open fun onSaveInstanceState(outState: Bundle) {}

    open fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                android.R.id.home -> {
                    context.supportFinishAfterTransition()
                    true
                }
                R.id.action_settings -> {
                    startActivity(Intent(context, SettingsActivity::class.java))
                    true
                }
                R.id.action_force_refresh -> {
                    startActivity(LoaderPresenter.createStartIntent(context))
                    true
                }
                R.id.action_refresh_images -> {
                    DDragon.deleteChampionImages()
                    startActivity(LoaderPresenter.createStartIntent(context))
                    true
                }
                else -> false
            }

    protected fun getString(@StringRes key: Int): String = context.getString(key)

    @ColorInt
    protected fun getColor(@ColorRes key: Int, theme: Resources.Theme? = context.theme): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(key, theme)
        } else {
            context.resources.getColor(key)
        }
    }

    protected fun getDrawable(@DrawableRes key: Int): Drawable? = context.getDrawable(key)

    protected fun getInteger(@IntegerRes key: Int): Int = context.resources.getInteger(key)

    open fun openChampionActivity(view: View, options: Bundle? = Bundle.EMPTY) {
        startActivityWithFade(Intent(context, ChampionActivity::class.java), options)
    }

    protected fun startActivity(intent: Intent, options: Bundle? = null) = context.startActivity(intent, options)

    protected fun startActivityWithFade(intent: Intent, options: Bundle? = Bundle.EMPTY) {
        val fade = ActivityOptionsCompat
                .makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
        val bundle = fade.toBundle()
        bundle?.putAll(options)
        startActivity(intent, bundle)
    }
}