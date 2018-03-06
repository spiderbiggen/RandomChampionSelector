package com.spiderbiggen.randomchampionselector.storage.database.callbacks;

import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.List;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 * <p>
 * Defines all interactions that load data
 */
public interface IDataInteractor {

    void findChampionList(final OnFinishedChampionListListener listener, final String role);

    void findChampionList(final OnFinishedChampionListListener listener);

    void findRoleList(final OnFinishedRolesListener listener);

    void findChampion(final OnFinishedChampionListener listener, final int championKey);

    void findRandomChampion(final OnFinishedChampionListener listener, final String role, final Champion champion);

    void findRandomChampion(final OnFinishedChampionListener listener, final Champion champion);

    void findAbilities(final OnFinishedAbilitiesListener listener, final Champion champion);

    interface OnFinishedChampionListener {
        void onFinishedChampionLoad(Champion champion);
    }

    interface OnFinishedChampionListListener {
        void onFinishedChampionListLoad(List<Champion> champions);
    }

    interface OnFinishedRolesListener {
        void onFinishedRoleListLoad(List<String> roles);
    }

    interface OnFinishedAbilitiesListener {
        void onFinishedAbilitiesLoad(List<Ability> abilities);
    }

    interface OnFinishedListener extends OnFinishedAbilitiesListener, OnFinishedChampionListener, OnFinishedChampionListListener, OnFinishedRolesListener {}
}
