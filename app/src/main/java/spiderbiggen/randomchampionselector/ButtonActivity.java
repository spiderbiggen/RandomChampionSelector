package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;


/**
 *
 */
public class ButtonActivity extends Activity {
    private ArrayList<Champion> champions = new ArrayList<>();

    private String TANK;
    private String FIGHTER;
    private String MAGE;
    private String ASSASSIN;
    private String SUPPORT;
    private String MARKSMAN;

    private String MANA;
    private String ENERGY;
    private String BLOOD_WELL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assignTranslatedStrings();

        populateChampions();
        setContentView(R.layout.activity_button);
    }


    public void pickRandomChampion(View view){
        Intent openChampionIntent = new Intent(this, ChampionActivity.class);
        startActivity(openChampionIntent);
    }

    public void assignTranslatedStrings(){
        TANK = this.getString(R.string.tank);
        FIGHTER = this.getString(R.string.fighter);
        MAGE = this.getString(R.string.mage);
        ASSASSIN = this.getString(R.string.assassin);
        SUPPORT = this.getString(R.string.support);
        MARKSMAN = this.getString(R.string.marksman);

        MANA = this.getString(R.string.mana);
        ENERGY = this.getString(R.string.energy);
        BLOOD_WELL = this.getString(R.string.blood_well);
    }

    public void populateChampions(){
        Champions.CHAMPIONS = new Champion[]{
                new Champion("Aatrox", FIGHTER, 1983, 870, BLOOD_WELL, 150, 345),
                new Champion("Ahri", MAGE, 1874, 1184, MANA, 550, 330),
                new Champion("Akali", ASSASSIN, 2033, 200, ENERGY, 125, 350),
                new Champion("Alistar", TANK,  2347, 925, MANA, 125, 330),
                new Champion("Amumu", TANK, 2041, 967, MANA, 125, 335),
                new Champion("Anivia", MAGE, 1658, 1247, MANA, 600, 325),
                new Champion("Annie", MAGE, 1804, 1184, MANA, 625, 335),
                new Champion("Ashe", MARKSMAN, 1871, 827, MANA, 600, 325),
                new Champion("Azir", MAGE, 1884, 1065, MANA, 525, 335),
                new Champion("Bard", SUPPORT, 1980, 1200, MANA, 500, 330),
                new Champion("Blitzcrank", TANK, 2198, 947, MANA, 125, 325),
                new Champion("Brand", MAGE, 1800, 1091, MANA, 550, 340),
                new Champion("Braum", SUPPORT, 2055, 1076, MANA, 125, 335)
        };
    }

}
