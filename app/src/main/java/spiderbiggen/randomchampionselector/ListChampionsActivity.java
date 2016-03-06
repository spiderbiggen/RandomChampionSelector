package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.Champions;

public class ListChampionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_champions);

        ListView lv = (ListView) findViewById(R.id.championList);
        ChampionAdapter ca = new ChampionAdapter(this, R.layout.list_champion_item, Champions.CHAMPIONS);
        lv.setAdapter(ca);

        final Context context = this;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Champion c = (Champion) parent.getItemAtPosition(position);

                Intent openChampionIntent = new Intent(context, ChampionActivity.class);
                openChampionIntent.putExtra("IsRandom", false);
                openChampionIntent.putExtra("Champion", c);
                startActivity(openChampionIntent);
            }
        });

    }
}
