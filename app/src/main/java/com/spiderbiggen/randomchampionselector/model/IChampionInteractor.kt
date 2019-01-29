package com.spiderbiggen.randomchampionselector.model

import android.graphics.Bitmap
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.domain.Champion
import io.reactivex.functions.Consumer

interface IChampionInteractor {

    fun findChampion(listener: IChampionCallBack, championKey: Int)
    fun findChampion(consumer: Consumer<Champion>, championKey: Int)

    fun findRandomChampion(listener: IChampionCallBack, championKey: Int? = null, role: String? = null)
    fun findRandomChampion(consumer: Consumer<Champion>, championKey: Int? = null, role: String? = null)

    fun findChampionList(listener: IChampionListCallback, role: String? = null)
    fun findChampionList(consumer: Consumer<List<Champion>>, role: String? = null)

    fun findImageForChampion(listener: BitmapCache.BitmapCallback, champion: Champion, vWidth: Int? = null, vHeight: Int? = null)
    fun findImageForChampion(listener: Consumer<Bitmap>, champion: Champion, vWidth: Int? = null, vHeight: Int? = null)

    interface IChampionCallBack {
        fun setChampion(champion: Champion)
    }

    interface IChampionListCallback {
        fun setChampionList(champions: Collection<Champion>)
    }

}