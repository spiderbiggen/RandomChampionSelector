package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.os.Build
import androidx.annotation.*
import com.spiderbiggen.randomchampionselector.model.IRequiresContext

@Suppress("UNUSED")
object ResourceManager : IRequiresContext {

    private lateinit var resources: Resources
    private lateinit var theme: Resources.Theme

    override fun useContext(context: Context) {
        resources = context.applicationContext.resources
        theme = context.applicationContext.theme
    }

    override fun hasContext(): Boolean = this::resources.isInitialized

    @Throws(NotFoundException::class)
    fun getString(@StringRes resource: Int): String = resources.getString(resource)

    @Throws(NotFoundException::class)
    fun getString(@StringRes resource: Int, vararg args: String): String = resources.getString(resource, args)

    fun getString(@StringRes resource: Int, default: String): String =
            try {
                getString(resource)
            } catch (_: NotFoundException) {
                default
            }

    @Throws(NotFoundException::class)
    fun getInt(@IntegerRes resource: Int): Int = resources.getInteger(resource)

    fun getInt(@IntegerRes resource: Int, default: Int): Int =
            try {
                getInt(resource)
            } catch (_: NotFoundException) {
                default
            }

    @Throws(NotFoundException::class)
    fun getLong(@IntegerRes resource: Int): Long = getInt(resource).toLong()

    fun getLong(@IntegerRes resource: Int, default: Long): Long =
            try {
                getLong(resource)
            } catch (_: NotFoundException) {
                default
            }

    @ColorInt
    @Throws(NotFoundException::class)
    @Suppress("DEPRECATION")
    fun getColor(@ColorRes resource: Int, theme: Resources.Theme = this.theme): Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(resource, theme)
            } else {
                resources.getColor(resource)
            }

    @Throws(NotFoundException::class)
    fun getDimensionPixelSize(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

    @Throws(NotFoundException::class)
    fun getDimension(@DimenRes resource: Int): Float = resources.getDimension(resource)

}