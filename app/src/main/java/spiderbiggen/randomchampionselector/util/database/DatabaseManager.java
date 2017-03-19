package spiderbiggen.randomchampionselector.util.database;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.util.List;

import spiderbiggen.randomchampionselector.IDataInteractor;
import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.ChampionRole;
import spiderbiggen.randomchampionselector.util.JsonParser;

/**
 *
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */

public class DatabaseManager implements IDataInteractor{

    private static DatabaseManager instance = new DatabaseManager();

    private ChampionsDBHelper championDbHelper;
    private AbilitiesDBHelper abilitiesDBHelper;

    private int maxHealth = 0;
    private int minHealth = 0;

    private DatabaseManager() {}

    public void useContext(Context context) {
        championDbHelper = new ChampionsDBHelper(context);
        //abilitiesDBHelper = new AbilitiesDBHelper(context);
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void updateDatabase(JsonParser parser) {
        if(championDbHelper.isOutdated() || championDbHelper.isEmpty()) {
            try {
                parser.readFile("champions.json");
                addChampions(parser.getChampions());
                championDbHelper.setOutdated(false);
            } catch (IOException e) {
                //
            }
        }
    }

    public void addChampions(final List<Champion> champions) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for(Champion champion: champions) {
                    addChampion(champion);
                }
            }
        });
    }

    public long addChampion(Champion champion) {
        return championDbHelper.addChampion(champion);
    }

    private String convertToRoleString(ChampionRole role) {
        return (role == ChampionRole.NONE)
                ? "%"
                : role.toString();
    }

    @Override
    public void findChampionList(final OnFinishedListener listener, final ChampionRole role) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String roleString = convertToRoleString(role);
                listener.onFinishedChampionListLoad(championDbHelper.getChampions(roleString));
            }
        });
    }

    @Override
    public void findChampion(final OnFinishedListener listener, final Champion champion) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listener.onFinishedChampionLoad(championDbHelper.getChampion(champion.getName()));
            }
        });
    }

    @Override
    public void findRandomChampion(final OnFinishedListener listener, final ChampionRole role, final Champion champion) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String roleString = convertToRoleString(role);
                listener.onFinishedChampionLoad(championDbHelper.getRandomChampion(roleString, champion));
            }
        });
    }

    @Override
    public void findAbilities(final OnFinishedListener listener, final Champion champion) {
        throw new UnsupportedOperationException("Function not yet implemented");
    }

    public int getMaxHealth() {
        return maxHealth > 0 ? maxHealth: championDbHelper.getMaxHealth();
    }

    public int getMinHealth() {
        return minHealth > 0 ? minHealth : championDbHelper.getMinHealth();
    }
}
