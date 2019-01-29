package com.spiderbiggen.randomchampionselector.model

import com.spiderbiggen.randomchampionselector.domain.Champion

interface IChampionInteractor {

    fun findChampion(championKey: Int, consumer: (Champion) -> Unit)

    fun findRandomChampion(consumer: (Champion) -> Unit, championKey: Int? = null, role: String? = null)

    fun findChampionList(consumer: (Collection<Champion>) -> Unit, role: String? = null)

}