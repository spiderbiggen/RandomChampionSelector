package com.spiderbiggen.randomchampionselector.presentation.extensions

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun LifecycleOwner.doOnDestroy(block: () -> Unit) {
    val observer = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            block()
            lifecycle.removeObserver(this)
        }
    }
    lifecycle.addObserver(observer)
}