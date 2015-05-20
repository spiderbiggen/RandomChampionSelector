package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        Intent openChampionIntent = new Intent(this, ChampionActivity.class);
        openChampionIntent.putExtra("Type", (String) spinner.getSelectedItem());
        startActivity(openChampionIntent);
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
    }
}
