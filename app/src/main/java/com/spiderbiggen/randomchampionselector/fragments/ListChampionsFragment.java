package com.spiderbiggen.randomchampionselector.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.spiderbiggen.randomchampionselector.IDataInteractor;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.adapters.ChampionAdapter;
import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;

public class ListChampionsFragment extends Fragment implements IDataInteractor.OnFinishedListener {

    private ChampionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.activity_list_champions, container, false);

        ListView lv = view.findViewById(R.id.championList);
        adapter = new ChampionAdapter(view.getContext(), R.layout.list_champion_item, new ArrayList<Champion>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Champion champion = (Champion) parent.getItemAtPosition(position);
                onFinishedChampionLoad(champion);
            }
        });

        return view;
    }

    @Override
    public void onFinishedChampionListLoad(final List<Champion> champions) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setChampions(champions);
            }
        });
    }

    @Override
    public void onFinishedChampionLoad(final Champion champion) {
        FragmentManager fragmentManager = getFragmentManager();
        final ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsFragment listFragment = (ListChampionsFragment) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().show(championFragment).hide(listFragment).commit();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                championFragment.updateChampion(champion);
            }
        });
    }

    @Override
    public void onFinishedRoleListLoad(List<String> roles) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }
}
