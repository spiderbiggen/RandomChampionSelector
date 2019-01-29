package com.spiderbiggen.randomchampionselector.model

interface IRiotData {

    val shouldRefresh: Boolean

    fun verifyImages(progress: IProgressCallback, finished: () -> Unit)

    fun update(progress: IProgressCallback, finished: () -> Unit)

}