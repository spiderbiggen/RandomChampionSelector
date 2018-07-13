package com.spiderbiggen.randomchampionselector.presenters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IRiotData
import com.spiderbiggen.randomchampionselector.views.activities.ListChampionsActivity
import com.spiderbiggen.randomchampionselector.views.activities.LoaderActivity
import java.util.*

/**
 * Created on 6-7-2018.
 * @author Stefan Breetveld
 */
class LoaderPresenter(context: LoaderActivity) : AbstractPresenter<LoaderActivity>(context), IProgressCallback, IRiotData.OnFinished {

    private var shouldRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        shouldRefresh = context.intent.getBooleanExtra(FORCE_REFRESH, false)
    }

    override fun onResume() =
            when {
                shouldRefresh || dataManager.shouldRefresh -> {
                    PreferenceManager.lastSync = -1
                    dataManager.update(this, this)
                }
                else -> dataManager.verifyImages(this, this)
            }

    override fun onFinished() {
        PreferenceManager.lastSync = Date().time
        val intent = Intent(context, ListChampionsActivity::class.java)
        context.startActivity(intent)
    }

    override fun onProgressUpdate(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
        context.updateProgressBar(type, progress, progressMax)
    }

    override fun finishExecution() = Unit

    companion object {

        const val FORCE_REFRESH = "FORCE_REFRESH"
        const val MILLIS_IN_MINUTE = 60_000

        fun createStartIntent(context: Context, forceRefresh: Boolean = true): Intent {
            val intent = Intent(context, LoaderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(FORCE_REFRESH, forceRefresh)
            return intent
        }
    }

}