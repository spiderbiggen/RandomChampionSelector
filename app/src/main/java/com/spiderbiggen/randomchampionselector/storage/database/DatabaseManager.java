package com.spiderbiggen.randomchampionselector.storage.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */
public class DatabaseManager implements IDataInteractor {

    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static DatabaseManager instance = new DatabaseManager();
    private SimpleDatabase database;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void useContext(Context context) {
        database = Room.databaseBuilder(context, SimpleDatabase.class, "random_champion_main").fallbackToDestructiveMigration().build();
    }

    public Disposable addChampions(final Collection<Champion> champions) {
        return Observable.just(champions)
                .subscribeOn(Schedulers.io())
                .subscribe(champions1 -> database.championDAO().insertAll(champions1));
    }

    @Override
    public Disposable findRoleList(final Consumer<List<String>> listener) {
        return database.championDAO().getAllRoles()
                .subscribeOn(Schedulers.io())
                .map(strings -> {
                    Set<String> roles = new TreeSet<>();
                    roles.add("All");
                    for (String string : strings) {
                        if (string == null) continue;
                        Collections.addAll(roles, string.split(","));
                    }
                    return new ArrayList<>(roles);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    @Override
    public Disposable findChampionList(final Consumer<List<Champion>> listener) {
        return findChampionList(listener, null);
    }

    @Override
    public Disposable findChampionList(final Consumer<List<Champion>> listener, final String role) {
        return getChampionListFlowable(role)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    @Override
    public Disposable findChampion(final Consumer<Champion> listener, final int championKey) {
        return database.championDAO().getChampion(championKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    @Override
    public Disposable findRandomChampion(final Consumer<Champion> listener, final int championKey) {
        return findRandomChampion(listener, null, championKey);
    }

    @Override
    public Disposable findRandomChampion(final Consumer<Champion> listener, final String role, final int championKey) {
        return getRandomChampionSingle(role)
                .subscribeOn(Schedulers.io())
                .repeat()
                .takeUntil(champion -> champion.getKey() != championKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    private Flowable<List<Champion>> getChampionListFlowable(String role) {
        return isWildcardRole(role) ? database.championDAO().getAll() : database.championDAO().getAll(role);
    }

    private Flowable<Champion> getRandomChampionSingle(String role) {
        return isWildcardRole(role) ? database.championDAO().getRandom() : database.championDAO().getRandom(role);
    }

    private boolean isWildcardRole(String role) {
        return role == null || role.isEmpty() || role.equalsIgnoreCase("all");
    }
}
