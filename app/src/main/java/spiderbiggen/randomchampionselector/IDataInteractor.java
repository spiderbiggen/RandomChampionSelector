package spiderbiggen.randomchampionselector;

import java.util.List;

import spiderbiggen.randomchampionselector.champion.Ability;
import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.ChampionRole;

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
        void onFinishedAbilitiesLoad(List<Ability> abilities);
    }

    void findChampionList(final OnFinishedListener listener, final ChampionRole role);
    void findChampion(final OnFinishedListener listener, final Champion champion);
    void findRandomChampion(final OnFinishedListener listener, final ChampionRole role, final Champion champion);
    void findAbilities(final OnFinishedListener listener, final Champion champion);
}
