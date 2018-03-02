package com.spiderbiggen.randomchampionselector;

import java.util.List;

import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 *
 * Defines all interactions that load data
 */
public interface IDataInteractor {

    interface OnFinishedListener {
        void onFinishedChampionListLoad(List<Champion> champions);
        void onFinishedChampionLoad(Champion champion);
        void onFinishedRoleListLoad(List<String> roles);
        void onFinishedAbilitiesLoad(List<Ability> abilities);
    }

    void findChampionList(final OnFinishedListener listener, final String role);
    void findChampionList(final OnFinishedListener listener);
    void findRoleList(final OnFinishedListener listener);
    void findChampion(final OnFinishedListener listener, final Champion champion);
    void findRandomChampion(final OnFinishedListener listener, final String role, final Champion champion);
    void findRandomChampion(final OnFinishedListener listener, final Champion champion);
    void findAbilities(final OnFinishedListener listener, final Champion champion);
}
