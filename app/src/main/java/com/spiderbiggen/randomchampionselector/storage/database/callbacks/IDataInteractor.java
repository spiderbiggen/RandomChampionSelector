package com.spiderbiggen.randomchampionselector.storage.database.callbacks;

import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 * <p>
 * Defines all interactions that load data
 */
public interface IDataInteractor {

    Disposable findChampionList(final Consumer<List<Champion>> listener, final String role);

    Disposable findChampionList(final Consumer<List<Champion>> listener);

    Disposable findRoleList(final Consumer<List<String>> listener);

    Disposable findChampion(final Consumer<Champion> listener, final int championKey);

    Disposable findRandomChampion(final Consumer<Champion> listener, final String role, final Champion champion);

    Disposable findRandomChampion(final Consumer<Champion> listener, final Champion champion);

//    Disposable findAbilities(final Consumer<Ability> listener, final Champion champion);

}
