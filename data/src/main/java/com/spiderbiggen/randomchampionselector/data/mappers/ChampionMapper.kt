package com.spiderbiggen.randomchampionselector.data.mappers

import com.spiderbiggen.randomchampionselector.data.ddragon.models.ApiChampion
import com.spiderbiggen.randomchampionselector.data.storage.database.models.ChampionEntity
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import javax.inject.Inject

class ChampionMapper @Inject constructor(private val infoMapper: InfoMapper) {
    fun fromApi(c: ApiChampion): Champion = Champion(
        key = c.key,
        id = c.id,
        name = c.name,
        title = c.title,
        lore = c.lore,
        blurb = c.blurb,
        roles = c.roles.toList(),
        info = infoMapper.fromApi(c.info)
    )

    fun toDatabase(c: Champion): ChampionEntity = ChampionEntity(
        key = c.key,
        id = c.id,
        name = c.name,
        title = c.title,
        lore = c.lore,
        blurb = c.blurb,
        roles = c.roles,
        info = infoMapper.toDatabase(c.info)
    )

    fun fromDatabase(c: ChampionEntity): Champion = Champion(
        key = c.key,
        id = c.id,
        name = c.name,
        title = c.title,
        lore = c.lore,
        blurb = c.blurb,
        roles = c.roles.toList(),
        info = infoMapper.fromDatabase(c.info)
    )
}