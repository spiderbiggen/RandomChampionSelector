package com.spiderbiggen.randomchampionselector.presentation.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import com.spiderbiggen.randomchampionselector.presentation.ui.common.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <R, T : State<R>> Fragment.collectScreenState(flow: Flow<T>, callback: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(callback)
        }
    }
}

fun <VB : ViewBinding> Fragment.viewBindings(viewBindingFactory: (View) -> VB) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

class FragmentViewBindingDelegate<VB : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {
    private var binding: VB? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
                val viewLifecycleOwner = it ?: return@Observer

                viewLifecycleOwner.doOnDestroy {
                    binding = null
                }
            }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return viewBindingFactory(thisRef.requireView()).also {
            this.binding = it.apply {
                val fragmentDescription = it::class.java.simpleName
                    .replaceFirst("Fragment", "")
                    .replace("Binding", "") +
                        "Fragment"
                root.contentDescription = fragmentDescription
            }
        }
    }
}