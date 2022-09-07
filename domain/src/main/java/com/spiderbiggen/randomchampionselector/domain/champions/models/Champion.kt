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
    val capitalizedTitle: String
        get() = title.substring(0, 1).uppercase(Locale.ENGLISH) + title.substring(1)

}