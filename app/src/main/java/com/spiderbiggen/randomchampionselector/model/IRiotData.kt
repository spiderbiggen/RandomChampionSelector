package com.spiderbiggen.randomchampionselector.model

interface IRiotData {

    val shouldRefresh: Boolean

    fun verifyImages(progress: IProgressCallback, finished: OnFinished)

    fun update(progress: IProgressCallback, finished: OnFinished)

    interface OnFinished {
        fun onFinished()
    }

}