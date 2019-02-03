package com.spiderbiggen.randomchampionselector.model

import android.content.Context

/**
 * Tells other classes that this class requires a [Context] to actually work.
 */
interface Contextual {

    /**
     * Tell the [Contextual] object what [context] to use.
     *
     * @param context any [Context]
     */
    fun useContext(context: Context)
}