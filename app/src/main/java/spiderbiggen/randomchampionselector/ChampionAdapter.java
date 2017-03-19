package spiderbiggen.randomchampionselector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.util.StringHolder;

/**
 * Created by Stefan on 5-8-2015.
 */
public class ChampionAdapter extends ArrayAdapter<Champion> {

    private final Context context;

    public ChampionAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public ChampionAdapter(Context context, int resource, List<Champion> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StringHolder strings = StringHolder.getInstance();
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

            if (imgV != null) {
                String formattedString = c.getName().replace(" ", "").replace("'", "").replace(".", "").toLowerCase() + "square";
                int resID = context.getResources().getIdentifier(formattedString, "drawable", context.getPackageName());
                Drawable image = getMyDrawable(resID);
                imgV.setImageDrawable(image);
            }
            if(nameV != null) {
                nameV.setText(c.getName());
            }
            if (roleV != null) {
                roleV.setText(strings.roleEnumToString(c.getRole()));
            }
        }

        return v;
    }

    @SuppressWarnings("deprecation")
    private Drawable getMyDrawable(int id) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getResources().getDrawableForDensity(id, dpi, context.getTheme());
        } else {
            return context.getResources().getDrawableForDensity(id, dpi);
        }
    }

    public void setChampions(List<Champion> champions) {
        this.clear();
        this.addAll(champions);
        this.notifyDataSetChanged();
    }
}
