package com.spiderbiggen.randomchampionselector.storage.database.callbacks

import com.spiderbiggen.randomchampionselector.model.Champion

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * Defines all interactions that load data.
 * Created by Stefan Breetveld on 18-3-2017.
 */
internal interface IDataInteractor {

    fun findRoleList(listener: Consumer<List<String>>): Disposable

    fun findChampion(listener: Consumer<Champion>, championKey: Int): Disposable

    fun findChampionList(listener: Consumer<List<Champion>>, role: String? = null): Disposable

    fun findRandomChampion(listener: Consumer<Champion>, championKey: Int, role: String? = null): Disposable

    //    Disposable findAbilities(final Consumer<Ability> listener, final Champion champion);

}
