package com.spiderbiggen.randomchampionselector.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.callback.ImageCallback;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.ImageType;
import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.List;
import java.util.Objects;

public class ChampionAdapter extends ArrayAdapter<Champion> {

    private static final String TAG = ChampionAdapter.class.getSimpleName();
    private final Context context;

    public ChampionAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public ChampionAdapter(Context context, int resource, List<Champion> items) {
        super(context, resource, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_champion_item, null);
        }

        ViewWrapper viewWrapper = new ViewWrapper(v, position);
        Champion c = getItem(position);
        if (c != null) {
            new DDragon(context).getChampionImage(c, ImageType.SQUARE, viewWrapper);
            TextView nameV = v.findViewById(R.id.championName);
            if (nameV != null) {
                nameV.setText(c.getName());
            }
            TextView titleV = v.findViewById(R.id.championTitle);
            if (titleV != null) {
                titleV.setText(c.getCapitalizedTitle());
            }
        }

        return v;
    }

    public void setChampions(List<Champion> champions) {
        this.setNotifyOnChange(false);
        this.clear();
        this.addAll(champions);
        this.notifyDataSetChanged();
        this.setNotifyOnChange(true);
    }

    private class ViewWrapper implements ImageCallback {

        private View view;
        private int position;

        public ViewWrapper(View view, int position) {
            this.view = view;
            this.position = position;
        }

        @Override
        public void setImage(Bitmap bitmap, Champion champion, ImageType type) {
            if (type != ImageType.SQUARE) return;
            if (Objects.equals(getItem(position), champion)) {
                ImageView imgV = view.findViewById(R.id.championIcon);
                if (imgV != null && bitmap != null) {
                    imgV.setImageBitmap(bitmap);
                }
            }
        }
    }
}
