package com.spiderbiggen.randomchampionselector.model

import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.domain.Champion

interface IChampionInteractor {

    fun findChampion(listener: IChampionCallBack, championKey: Int)

    fun findRandomChampion(listener: IChampionCallBack, championKey: Int? = null, role: String? = null)

    fun findChampionList(listener: IChampionListCallback, role: String? = null)

    fun findImageForChampion(listener: BitmapCache.BitmapCallback, champion: Champion, vWidth: Int? = null, vHeight: Int? = null)

    interface IChampionCallBack {
        fun setChampion(champion: Champion)
    }

    interface IChampionListCallback {
        fun setChampionList(champions: Collection<Champion>)
    }
}