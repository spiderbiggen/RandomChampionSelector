package com.spiderbiggen.randomchampionselector.domain.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created on 2019-01-30.
 * @author Stefan Breetveld
 */

/**
 * Run the given mapping function in parallel.
 *
 * @param context a [CoroutineDispatcher] that defines how the mapper is parallelized
 * @param function a mapper function that maps [A] to [B] needs to be a coroutine
 * @return [A] mapped to [B]
 */
suspend fun <A, B> Iterable<A>.mapAsync(
    context: CoroutineContext = Dispatchers.Default,
    function: suspend (A) -> B
): Iterable<B> = withContext(context) {
    map { async { function(it) } }.map { it.await() }
}

/**
 * Make sure the given function is run on the main thread, great for updating UI Elements.
 *
 * @param function function that should be run on the main thread
 */
suspend fun onMainThread(function: () -> Unit) {
    withContext(Dispatchers.Main) { function() }
}