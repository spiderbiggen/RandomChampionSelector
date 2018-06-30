package com.spiderbiggen.randomchampionselector.view.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;

import java.io.IOException;
import java.util.List;

import static com.spiderbiggen.randomchampionselector.ddragon.DDragon.getInstance;

public class ChampionAdapter extends RecyclerView.Adapter<ChampionAdapter.ViewHolder> {

    private static final String TAG = ChampionAdapter.class.getSimpleName();

    private final View.OnClickListener clickListener;
    private List<Champion> champions;

    public ChampionAdapter(final List<Champion> champions, final View.OnClickListener clickListener) {
        this.champions = champions;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_champion_item, parent, false);
        v.setOnClickListener(clickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Champion champion = champions.get(position);

        DDragon dDragon = getInstance();
        try {
            Bitmap backGround = dDragon.getChampionBitmap(champion, ImageType.SPLASH);
            viewHolder.backGroundView.setImageBitmap(backGround);
        } catch (IOException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }
        viewHolder.nameView.setText(champion.getName());
        viewHolder.titleView.setText(champion.getCapitalizedTitle());
    }

    @Override
    public int getItemCount() {
        return champions != null ? champions.size() : 0;
    }

    public void setChampions(List<Champion> champions) {
        this.champions = champions;
        notifyDataSetChanged();
    }

    public Champion getChampion(int position) {
        if (position > getItemCount() || position < 0) return null;
        return champions.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView backGroundView;
        private TextView nameView;
        private TextView titleView;

        private ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.champion_name);
            titleView = itemView.findViewById(R.id.champion_title);
            backGroundView = itemView.findViewById(R.id.champion_splash);
        }
    }
}
