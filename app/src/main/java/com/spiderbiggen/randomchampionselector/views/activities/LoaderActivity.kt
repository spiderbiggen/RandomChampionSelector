package com.spiderbiggen.randomchampionselector.views.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import kotlinx.android.synthetic.main.activity_loader.*
import java.util.*

class LoaderActivity : AbstractActivity(), IProgressCallback {
    private var shouldRefresh: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)
        shouldRefresh = intent.getBooleanExtra(FORCE_REFRESH, false)
    }

    override fun onResume() {
        super.onResume()
        when {
            shouldRefresh || dataManager.shouldRefresh -> {
                PreferenceManager.lastSync = -1
                dataManager.update(this, this::onFinished)
            }
            else -> dataManager.verifyImages(this, this::onFinished)
        }
    }

    private fun onFinished() {
        PreferenceManager.lastSync = Date().time
        val intent = Intent(this, ListChampionsActivity::class.java)
        startActivity(intent)
    }

    private fun updateProgressBar(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
        if (type === IProgressCallback.Progress.ERROR) {
            val progressDrawable = progressBar.indeterminateDrawable.mutate()
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
            progressBar.progressDrawable = progressDrawable
        }

        progressBar.isIndeterminate = type.indeterminate
        progressBar.progress = progress
        progressBar.max = progressMax

        progressText.text = getString(type.stringResource, progress, progressMax)
    }

    override fun onProgressUpdate(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
        updateProgressBar(type, progress, progressMax)
    }

}
