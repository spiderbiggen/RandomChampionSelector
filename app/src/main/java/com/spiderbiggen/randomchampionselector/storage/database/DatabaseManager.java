package com.spiderbiggen.randomchampionselector.storage.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.spiderbiggen.randomchampionselector.IDataInteractor;
import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */
public class DatabaseManager implements IDataInteractor {

    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static DatabaseManager instance = new DatabaseManager();
    private SimpleDatabase database;
    private ThreadPoolExecutor executor;

    private DatabaseManager() {
        executor = new ThreadPoolExecutor(0, 8, 200L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void useContext(Context context) {
        database = Room.databaseBuilder(context, SimpleDatabase.class, "random_champion_main").fallbackToDestructiveMigration().build();
    }

    public void addChampions(final List<Champion> champions) {
        Champion[] arr = new Champion[champions.size()];
        addChampions(champions.toArray(arr));
    }

    public void addChampions(Champion... champions) {
        database.championDAO().insertAll(champions);
    }

    @Override
    public void findRoleList(final OnFinishedListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> strings = database.championDAO().getAllRoles();
                Set<String> roles = new TreeSet<>();
                roles.add("All");
                for (String string : strings) {
                    if (string == null) continue;
                    Collections.addAll(roles, string.split(","));
                }
                listener.onFinishedRoleListLoad(new ArrayList<>(roles));
            }
        });
    }

    @Override
    public void findChampionList(final OnFinishedListener listener, final String role) {
        if (role == null || role.equalsIgnoreCase("all")) {
            findChampionList(listener);
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Champion> champions = database.championDAO().getAll(role);
                Log.d(TAG, "findChampionList: " + champions);
                listener.onFinishedChampionListLoad(champions);
            }
        });
    }

    @Override
    public void findChampionList(final OnFinishedListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Champion> champions = database.championDAO().getAll();
                Log.d(TAG, "findChampionList: " + champions);
                listener.onFinishedChampionListLoad(champions);
            }
        });
    }

    @Override
    public void findChampion(final OnFinishedListener listener, final Champion champion) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final Champion champion1 = database.championDAO().getChampion(champion.getKey());
                Log.d(TAG, "findChampion: " + champion1);
                listener.onFinishedChampionLoad(champion1);
            }
        });
    }

    @Override
    public void findRandomChampion(final OnFinishedListener listener, final String role, final Champion champion) {
        Log.d(TAG, "findRandomChampion() called with: listener = [" + listener + "], role = [" + role + "], champion = [" + champion + "]");
        if (role == null || role.equalsIgnoreCase("all")) {
            findRandomChampion(listener, champion);
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Champion champion1;
                do {
                    champion1 = database.championDAO().getRandom(role);
                } while (Objects.equals(champion, champion1));
                Log.d(TAG, "findRandomChampion: " + champion1);
                listener.onFinishedChampionLoad(champion1);
            }
        });
    }

    @Override
    public void findRandomChampion(final OnFinishedListener listener, final Champion champion) {
        Log.d(TAG, "findRandomChampion() called with: listener = [" + listener + "], champion = [" + champion + "]");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Champion champion1;
                do {
                    champion1 = database.championDAO().getRandom();
                } while (Objects.equals(champion, champion1));
                Log.d(TAG, "findRandomChampion: " + champion1);
                listener.onFinishedChampionLoad(champion1);
            }
        });
    }

    @Override
    public void findAbilities(final OnFinishedListener listener, final Champion champion) {
        throw new UnsupportedOperationException("Function not yet implemented");
    }
}
