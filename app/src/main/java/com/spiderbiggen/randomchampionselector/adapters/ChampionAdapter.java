package com.spiderbiggen.randomchampionselector.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;

import java.util.List;

public class ChampionAdapter extends RecyclerView.Adapter<ChampionAdapter.ViewHolder> {

    private static final String TAG = ChampionAdapter.class.getSimpleName();
    private final Context context;

    private final View.OnClickListener clickListener;
    private List<Champion> champions;

    public ChampionAdapter(final Context context, final List<Champion> champions, final View.OnClickListener clickListener) {
        this.context = context;
        this.champions = champions;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_champion_item, parent, false);
        v.setOnClickListener(clickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Champion champion = champions.get(position);

        DDragon dDragon = new DDragon(context);
        Bitmap icon = dDragon.getChampionBitmap(champion, ImageType.SQUARE);
        Bitmap backGround = dDragon.getChampionBitmap(champion, ImageType.SPLASH);
        holder.nameView.setText(champion.getName());
        holder.titleView.setText(champion.getCapitalizedTitle());
        holder.iconView.setImageBitmap(icon);
        holder.backGroundView.setImageBitmap(backGround);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView backGroundView;
        public ImageView iconView;
        public TextView nameView;
        public TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.championName);
            titleView = itemView.findViewById(R.id.championTitle);
            iconView = itemView.findViewById(R.id.championIcon);
            backGroundView = itemView.findViewById(R.id.champion_background);
        }
    }
}
