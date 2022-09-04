package com.spiderbiggen.randomchampionselector.domain.coroutines

import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class WeakLazyDelegate<T>(private val builder: () -> T) : ReadOnlyProperty<Any, T> {
    private var _t = WeakReference<T>(null)

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return _t.get() ?: builder().also { _t = WeakReference(it) }
    }
}

fun <T> weakLazy(builder: () -> T) = WeakLazyDelegate<T>(builder)