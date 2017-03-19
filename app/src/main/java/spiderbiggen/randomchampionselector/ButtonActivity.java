package spiderbiggen.randomchampionselector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import spiderbiggen.randomchampionselector.champion.ChampionRole;
import spiderbiggen.randomchampionselector.util.JsonParser;
import spiderbiggen.randomchampionselector.util.StringHolder;
import spiderbiggen.randomchampionselector.util.database.DatabaseManager;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public class ButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new StringHolder(this);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.useContext(this);
        JsonParser parser = new JsonParser(getAssets());
        databaseManager.updateDatabase(parser);

        setContentView(R.layout.activity_button);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChampionFragment fragment = new ChampionFragment();
        ListChampionsActivity listFragment = new ListChampionsActivity();
        if (fragmentManager.findFragmentByTag("rngChampion") == null) {
            fragmentTransaction.add(R.id.fragment_container, fragment, "rngChampion");
        }
        if (fragmentManager.findFragmentByTag("listChampion") == null) {
            fragmentTransaction.add(R.id.fragment_container, listFragment, "listChampion").hide(listFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsActivity listFragment = (ListChampionsActivity) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().show(championFragment).hide(listFragment).commit();
    }

    public void openChampionList(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsActivity listFragment = (ListChampionsActivity) fragmentManager.findFragmentByTag("listChampion");
        DatabaseManager.getInstance().findChampionList(listFragment, ChampionRole.NONE);
        fragmentManager.beginTransaction().hide(championFragment).show(listFragment).commit();
    }

    public void pickRandomChampion(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsActivity listFragment = (ListChampionsActivity) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().show(championFragment).hide(listFragment).commit();
        championFragment.reRollChampion(spinner.getSelectedItem().toString());
    }
}
