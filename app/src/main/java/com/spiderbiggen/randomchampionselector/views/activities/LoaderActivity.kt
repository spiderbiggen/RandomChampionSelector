package com.spiderbiggen.randomchampionselector.views.activities

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.presenters.LoaderPresenter
import kotlinx.android.synthetic.main.activity_loader.*

class LoaderActivity : AppCompatActivity() {

    private val loaderPresenter = LoaderPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)
        loaderPresenter.onCreate()
    }

    override fun onDestroy() {
        loaderPresenter.onDestroy()
        super.onDestroy()
    }

    fun updateProgressBar(type: IProgressCallback.Progress, progress: Int, progressMax: Int) {
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
}
