package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public class ButtonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assignTranslatedStrings();
        assignImages();
        Champions.populateChampions();

        setContentView(R.layout.activity_button);
        populateSpinner();
    }

    private void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        String all = this.getString(R.string.all);
        String[] types = new String[]{all, Champions.TANK, Champions.FIGHTER, Champions.MAGE, Champions.ASSASSIN, Champions.SUPPORT, Champions.MARKSMAN};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, R.id.textView, types);
        spinner.setAdapter(adapter);
    }


    public void pickRandomChampion(View view){
        Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        String all = this.getString(R.string.all);
        if(spinner.getSelectedItem().equals(all)) {
            Intent openChampionIntent = new Intent(this, ChampionActivity.class);
            startActivity(openChampionIntent);
        }else{
            Intent openChampionIntent = new Intent(this, ChampionWithTypeActivity.class);
            openChampionIntent.putExtra("Type", (String)spinner.getSelectedItem());
            startActivity(openChampionIntent);
        }
    }

    private void assignTranslatedStrings(){
        Champions.TANK = this.getString(R.string.tank);
        Champions.FIGHTER = this.getString(R.string.fighter);
        Champions.MAGE = this.getString(R.string.mage);
        Champions.ASSASSIN = this.getString(R.string.assassin);
        Champions.SUPPORT = this.getString(R.string.support);
        Champions.MARKSMAN = this.getString(R.string.marksman);

        Champions.MANA = this.getString(R.string.mana);
        Champions.ENERGY = this.getString(R.string.energy);
        Champions.BLOOD_WELL = this.getString(R.string.blood_well);


        ChampionNames.AATROX = this.getString(R.string.aatrox);
    }


    private void assignImages() {
        ChampionImages.AATROX = getMyDrawable(R.drawable.aatrox);
        ChampionImages.AHRI = getMyDrawable(R.drawable.ahri);
        ChampionImages.AKALI = getMyDrawable(R.drawable.akali);
        ChampionImages.ALISTAR = getMyDrawable(R.drawable.alistar);
        ChampionImages.AMUMU = getMyDrawable(R.drawable.amumu);
        ChampionImages.ANIVIA = getMyDrawable(R.drawable.anivia);
        ChampionImages.ANNIE = getMyDrawable(R.drawable.annie);
        ChampionImages.ASHE = getMyDrawable(R.drawable.ashe);
        ChampionImages.AZIR = getMyDrawable(R.drawable.azir);
        ChampionImages.BARD = getMyDrawable(R.drawable.bard);
        ChampionImages.BLITZCRANK = getMyDrawable(R.drawable.blitzcrank);
        ChampionImages.BRAND = getMyDrawable(R.drawable.brand);
        ChampionImages.BRAUM = getMyDrawable(R.drawable.braum);
        ChampionImages.CAITLYN = getMyDrawable(R.drawable.caitlyn);
        ChampionImages.CASSIOPEIA = getMyDrawable(R.drawable.cassiopeia);
        ChampionImages.CHOGATH = getMyDrawable(R.drawable.chogath);
        ChampionImages.CORKI = getMyDrawable(R.drawable.corki);
        ChampionImages.DARIUS = getMyDrawable(R.drawable.darius);
        ChampionImages.DIANA = getMyDrawable(R.drawable.diana);
        ChampionImages.DRMUNDO = getMyDrawable(R.drawable.drmundo);
        ChampionImages.DRAVEN = getMyDrawable(R.drawable.draven);
        ChampionImages.ELISE = getMyDrawable(R.drawable.elise);
        ChampionImages.EVELYNN = getMyDrawable(R.drawable.evelynn);
        ChampionImages.EZREAL = getMyDrawable(R.drawable.ezreal);
        ChampionImages.FIDDLESTICKS = getMyDrawable(R.drawable.fiddlesticks);
        ChampionImages.FIORA = getMyDrawable(R.drawable.fiora);
        ChampionImages.FIZZ = getMyDrawable(R.drawable.fizz);
    }

    @SuppressWarnings("deprecation")
    private Drawable getMyDrawable(int id) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        if (Build.VERSION.SDK_INT >= 21) {
            return this.getResources().getDrawableForDensity(id, dpi, this.getTheme());
        } else {
            return this.getResources().getDrawableForDensity(id, dpi);
        }
    }

}
