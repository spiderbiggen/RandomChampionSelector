package com.spiderbiggen.randomchampionselector.model

interface IRiotData {

    val shouldRefresh: Boolean

    suspend fun verifyImages(progress: IProgressCallback)

    suspend fun update(progress: IProgressCallback)

}