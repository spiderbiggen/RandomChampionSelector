package com.spiderbiggen.randomchampionselector.data

import kotlinx.coroutines.*

/**
 * Created on 2019-01-30.
 * @author Stefan Breetveld
 */

/**
 *
 */
suspend fun <A, B> Iterable<A>.mapAsync(context: CoroutineDispatcher = Dispatchers.Default, f: suspend (A) -> B): List<B> = runBlocking {
        map { async(context) { f(it) } }.map { it.await() }
}

/**
 * 
 */
suspend fun onMainThread(f: () -> Unit) {
    withContext(Dispatchers.Main) { f() }
}