package com.spiderbiggen.randomchampionselector.presentation.ui.champion.overview

import android.net.Uri
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.ChampionViewData

data class ChampionsOverviewViewData(
    val headerImage: Uri,
    val items: List<ChampionViewData>
)
