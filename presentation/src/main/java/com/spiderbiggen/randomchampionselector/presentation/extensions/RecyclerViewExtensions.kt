package com.spiderbiggen.randomchampionselector.presentation.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeAdapterOnDestroy(viewLifecycleOwner: LifecycleOwner) {
    viewLifecycleOwner.doOnDestroy {
        adapter = null
    }
}