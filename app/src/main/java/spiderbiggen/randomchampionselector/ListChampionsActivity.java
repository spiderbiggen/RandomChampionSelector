package spiderbiggen.randomchampionselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListChampionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_champions);

        ListView lv = (ListView) findViewById(R.id.championList);
        ChampionAdapter ca = new ChampionAdapter(this, R.layout.list_champion_item, Champions.CHAMPIONS);
        lv.setAdapter(ca);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Champion c =(Champion) parent.getItemAtPosition(position);
                System.out.println(c);
            }
        });
    }
}
