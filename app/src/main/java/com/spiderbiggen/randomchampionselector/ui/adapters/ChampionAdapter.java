package com.spiderbiggen.randomchampionselector.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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

import static com.spiderbiggen.randomchampionselector.ddragon.DDragon.createDDragon;

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

        DDragon dDragon = createDDragon(context);
        try {
            Bitmap backGround = dDragon.getChampionBitmap(champion, ImageType.SPLASH);
            holder.backGroundView.setImageBitmap(backGround);
        } catch (IOException e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }
        holder.nameView.setText(champion.getName());
        holder.titleView.setText(champion.getCapitalizedTitle());
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
            nameView = itemView.findViewById(R.id.championName);
            titleView = itemView.findViewById(R.id.championTitle);
            backGroundView = itemView.findViewById(R.id.champion_background);
        }
    }
}
