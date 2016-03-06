package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import spiderbiggen.randomchampionselector.champion.Champions;
import spiderbiggen.randomchampionselector.util.StringHolder;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public class ButtonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new StringHolder(this);
        Champions.populateChampions(this.getAssets());
        setContentView(R.layout.activity_button);
        populateSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_button);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainBg);
        Champions.pickRandomChampion(null, this.getString(R.string.all));
        populateSpinner();
    }

    private void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        /*String all = this.getString(R.string.all);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);*/
        /*adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);*/
    }

    public void openChampionList(View view){
        Intent openChampionIntent = new Intent(this, ListChampionsActivity.class);
        startActivity(openChampionIntent);
    }

    public void pickRandomChampion(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        Intent openChampionIntent = new Intent(this, ChampionActivity.class);
        openChampionIntent.putExtra("Type", spinner.getSelectedItem().toString());
        startActivity(openChampionIntent);
    }
}
