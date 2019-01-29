package com.spiderbiggen.randomchampionselector.data.storage.database

import com.spiderbiggen.randomchampionselector.domain.Champion

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

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

    fun findRoleList(listener: Consumer<List<String>>): Disposable

    fun findChampion(listener: Consumer<Champion>, championKey: Int): Disposable

    fun findChampionList(listener: Consumer<List<Champion>>, role: String? = null): Disposable

    fun findRandomChampion(listener: Consumer<Champion>, championKey: Int? = null, role: String? = null): Disposable

}
