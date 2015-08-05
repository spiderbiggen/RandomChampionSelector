package spiderbiggen.randomchampionselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Stefan on 5-8-2015.
 */
public class ChampionAdapter extends ArrayAdapter<Champion> {

    public ChampionAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ChampionAdapter(Context context, int resource, List<Champion> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_champion_item, null);
        }

        Champion c = getItem(position);

        if (c != null) {

            ImageView imgV = (ImageView) v.findViewById(R.id.championIcon);
            TextView nameV = (TextView) v.findViewById(R.id.championName);
            TextView roleV = (TextView) v.findViewById(R.id.championRole);

            if(nameV != null) {
                System.out.println("name");
                nameV.setText(c.getName());
            }
            System.out.println("nemeV = null");

            if (roleV != null) {
                roleV.setText(c.getRole());
            }
        }

        return v;
    }
}
