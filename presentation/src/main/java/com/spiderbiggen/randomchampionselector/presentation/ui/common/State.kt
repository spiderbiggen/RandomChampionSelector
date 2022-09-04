package com.spiderbiggen.randomchampionselector.presentation.ui.common

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Error<T>(val error: Throwable) : State<T>()
    data class Ready<T>(val viewData: T): State<T>()
}
