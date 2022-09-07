package com.spiderbiggen.randomchampionselector.data.mappers

import com.spiderbiggen.randomchampionselector.data.ddragon.models.ApiInfo
import com.spiderbiggen.randomchampionselector.data.storage.database.models.InfoEntity
import com.spiderbiggen.randomchampionselector.domain.champions.models.Info
import javax.inject.Inject

class InfoMapper @Inject constructor() {
    fun fromApi(i: ApiInfo): Info = Info(attack = i.attack, i.defense, i.magic, i.difficulty)

    fun toDatabase(i: Info): InfoEntity = InfoEntity(attack = i.attack, i.defense, i.magic, i.difficulty)

    fun fromDatabase(i: InfoEntity): Info = Info(attack = i.attack, i.defense, i.magic, i.difficulty)
}