package com.spiderbiggen.randomchampionselector.presentation.ui.champion

import android.net.Uri


data class ChampionViewData(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String? = null,
    val image: Uri,
)
