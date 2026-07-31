package com.spiderbiggen.randomchampionselector.presentation.ui.champion

import android.net.Uri
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import javax.inject.Inject

class MapChampionViewData @Inject constructor(
    private val fileRepository: FileRepository,
    private val preferenceRepository: PreferenceRepository,
) {
    operator fun invoke(champion: Champion) = ChampionViewData(
        id = champion.key,
        title = champion.name,
        subtitle = champion.capitalizedTitle(preferenceRepository.contentLocale),
        description = champion.lore,
        image = Uri.fromFile(fileRepository.getBitmapFile(champion)),
    )
}
