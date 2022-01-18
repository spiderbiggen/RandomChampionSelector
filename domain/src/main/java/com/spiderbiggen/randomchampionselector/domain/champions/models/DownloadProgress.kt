package com.spiderbiggen.randomchampionselector.domain.champions.models

sealed class DownloadProgress(val indeterminate: Boolean = true) {
    data class Error(val t: Throwable) : DownloadProgress()
    object NoInternet : DownloadProgress()
    object Idle : DownloadProgress()
    object CheckingVersion : DownloadProgress()
    object UpdateChampions : DownloadProgress()
    data class Validating(val completed: Int, val total: Int) : DownloadProgress(false)
    data class Unvalidated(val champions: List<Champion>) : DownloadProgress(false)
    data class Downloaded(val completed: Int, val total: Int) : DownloadProgress(false)
    object DownloadedSuccess : DownloadProgress(true)
    object Success : DownloadProgress()
}