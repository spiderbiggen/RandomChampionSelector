package com.spiderbiggen.randomchampionselector.util.data

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.spiderbiggen.randomchampionselector.interfaces.Contextual
import com.spiderbiggen.randomchampionselector.util.data.ResourceManager.resources

/**
 * Allows other classes to access [resources] without requiring a context.
 *
 * @author Stefan Breetveld
 */
object ResourceManager : Contextual {

    private lateinit var resources: Resources
    private lateinit var theme: Resources.Theme

    override fun useContext(context: Context) {
        resources = context.applicationContext.resources
        theme = context.applicationContext.theme
    }

    /**
     * Retrieves a [String] from the resources object
     * @see Resources.getString
     */
    @Throws(NotFoundException::class)
    fun getString(@StringRes resource: Int): String = resources.getString(resource)

    /**
     * Retrieves a [Int] from the resources object
     * @see Resources.getInteger
     */
    @Throws(NotFoundException::class)
    fun getInt(@IntegerRes resource: Int): Int = resources.getInteger(resource)

    /**
     * Retrieves a [ColorInt] from the resources object
     * @see Resources.getColor
     */
    @ColorInt
    @Throws(NotFoundException::class)
    @Suppress("DEPRECATION")
    fun getColor(@ColorRes resource: Int, theme: Resources.Theme = this.theme): Int = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> resources.getColor(resource, theme)
        else -> resources.getColor(resource)
    }
}