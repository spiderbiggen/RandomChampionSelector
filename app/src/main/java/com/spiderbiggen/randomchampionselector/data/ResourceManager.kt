package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.spiderbiggen.randomchampionselector.model.Contextual

object ResourceManager : Contextual {

    private lateinit var resources: Resources
    private lateinit var theme: Resources.Theme

    override fun useContext(context: Context) {
        resources = context.applicationContext.resources
        theme = context.applicationContext.theme
    }

    @Throws(NotFoundException::class)
    fun getString(@StringRes resource: Int): String = resources.getString(resource)

    @Throws(NotFoundException::class)
    fun getInt(@IntegerRes resource: Int): Int = resources.getInteger(resource)

    @ColorInt
    @Throws(NotFoundException::class)
    @Suppress("DEPRECATION")
    fun getColor(@ColorRes resource: Int, theme: Resources.Theme = this.theme): Int = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> resources.getColor(resource, theme)
        else -> resources.getColor(resource)
    }
}