package com.spiderbiggen.randomchampionselector.data.storage.database

import com.spiderbiggen.randomchampionselector.domain.Champion

import io.reactivex.disposables.Disposable

/**
 * Defines all interactions that load data.
 *
 * @author Stefan Breetveld
 */
interface IDataInteractor {

    fun addChampion(champion: Champion): Disposable

    fun addChampions(champions: Collection<Champion>): Disposable

    fun removeChampion(champion: Champion): Disposable

    fun removeChampions(champions: Collection<Champion>): Disposable

    fun updateChampion(champion: Champion): Disposable

    fun updateChampions(champions: Collection<Champion>): Disposable

    fun findRoleList(listener: (List<String>) -> Unit): Disposable

    fun findChampion(listener: (Champion) -> Unit, championKey: Int): Disposable

    fun findChampionList(listener: (List<Champion>) -> Unit, role: String? = null): Disposable

    fun findRandomChampion(listener: (Champion) -> Unit, championKey: Int? = null, role: String? = null): Disposable

}
