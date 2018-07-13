package com.spiderbiggen.randomchampionselector.views.adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.domain.Champion

class ChampionAdapter(champions: Collection<Champion>, private val clickListener: View.OnClickListener) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>() {

    private val champions = champions.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_champion_item, parent, false)
        v.setOnClickListener(clickListener)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val champion = champions[position]

        DDragon.getChampionBitmapFromCache(champion, viewHolder)

        viewHolder.nameView.text = champion.name
        viewHolder.titleView.text = champion.capitalizedTitle
    }

    override fun getItemCount(): Int = champions.size

    fun setChampions(champions: Collection<Champion>) {
        this.champions.clear()
        this.champions.addAll(champions)
        notifyDataSetChanged()
    }

    fun getChampion(position: Int): Champion? = champions.getOrNull(position)

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), BitmapCache.BitmapCallback {

        internal val imageView: ImageView = itemView.findViewById(R.id.champion_splash)
        internal val nameView: TextView = itemView.findViewById(R.id.champion_name)
        internal val titleView: TextView = itemView.findViewById(R.id.champion_title)

        override fun loadImageSuccess(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }

    }

}
