package spiderbiggen.randomchampionselector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import spiderbiggen.randomchampionselector.champion.Ability;
import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.util.database.DatabaseManager;

public class ListChampionsActivity extends Fragment implements IDataInteractor.OnFinishedListener{

    private final ListChampionsActivity instance = this;
    private ChampionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.activity_list_champions, container, false);


        ListView lv = (ListView) view.findViewById(R.id.championList);
        adapter = new ChampionAdapter(view.getContext(), R.layout.list_champion_item, new ArrayList<Champion>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Champion champion = (Champion) parent.getItemAtPosition(position);
                DatabaseManager.getInstance().findChampion(instance, champion);
            }
        });

        return view;
    }

    @Override
    public void onFinishedChampionListLoad(List<Champion> champions) {
        adapter.setChampions(champions);
    }

    @Override
    public void onFinishedChampionLoad(Champion champion) {
        FragmentManager fragmentManager = getFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsActivity listFragment = (ListChampionsActivity) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().show(championFragment).hide(listFragment).commit();
        championFragment.updateChampion(champion);
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }
}
