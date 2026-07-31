package com.spiderbiggen.randomchampionselector.domain.champions.models

import java.util.*

data class Champion(
    val key: Int,
    val id: String,
    val name: String,
    val title: String,
    val lore: String,
    val blurb: String,
    val roles: List<String>,
    val info: Info
) {
    /**
     * The [title] with its first character capitalized.
     *
     * @param locale the locale the champion data was fetched in, casing rules differ per language.
     * Some locales ship champions without a title, so an empty [title] is returned as is.
     */
    fun capitalizedTitle(locale: Locale): String = title.replaceFirstChar { it.uppercase(locale) }

}