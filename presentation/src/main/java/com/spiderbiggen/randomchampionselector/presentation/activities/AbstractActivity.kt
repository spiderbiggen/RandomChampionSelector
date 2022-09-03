package com.spiderbiggen.randomchampionselector.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

/**
 * Created on 2019-01-29.
 * @author Stefan Breetveld
 */
@FlowPreview
@AndroidEntryPoint
abstract class AbstractActivity : AppCompatActivity {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    private val viewModel: BaseViewModel by viewModels()

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            supportFinishAfterTransition()
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        R.id.action_force_refresh -> {
            startActivity(createStartIntent())
            true
        }
        R.id.action_refresh_images -> {
            viewModel.clearImages()
            startActivity(createStartIntent())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    open fun openChampionActivity(options: Bundle? = Bundle.EMPTY) =
        startActivityWithFade(Intent(this, ChampionActivity::class.java), options)

    protected fun startActivityWithFade(intent: Intent, options: Bundle? = Bundle.EMPTY) {
        val fade = ActivityOptionsCompat
            .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        val bundle = fade.toBundle()
        bundle?.putAll(options)
        startActivity(intent, bundle)
    }

    fun createStartIntent(forceRefresh: Boolean = true): Intent {
        val intent = Intent(this, LoaderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(FORCE_REFRESH, forceRefresh)
        return intent
    }

    companion object {
        const val FORCE_REFRESH = "FORCE_REFRESH"
    }
}