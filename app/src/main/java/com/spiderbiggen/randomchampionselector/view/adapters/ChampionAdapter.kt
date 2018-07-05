package com.spiderbiggen.randomchampionselector.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.model.Champion
import com.spiderbiggen.randomchampionselector.model.ImageType

class ChampionAdapter(private var champions: List<Champion>?, private val clickListener: View.OnClickListener) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_champion_item, parent, false)
        v.setOnClickListener(clickListener)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val champion = champions!![position]

        DDragon.getChampionBitmapFromCache(champion, ImageType.SPLASH, viewHolder.imageView)
//        try {
//            val backGround = DDragon.getChampionBitmap(champion, ImageType.SPLASH)
//            viewHolder.imageView.setImageBitmap(backGround)
//        } catch (e: IOException) {
//            Log.e(TAG, "onBindViewHolder: ", e)
//        }

        viewHolder.nameView.text = champion.name
        viewHolder.titleView.text = champion.capitalizedTitle
    }

    override fun getItemCount(): Int {
        return champions?.size ?: 0
    }

    fun setChampions(champions: List<Champion>) {
        this.champions = champions
        notifyDataSetChanged()
    }

    fun getChampion(position: Int): Champion? {
        return when {
            position > itemCount || position < 0 -> null
            else -> champions?.get(position)
        }
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val imageView: ImageView = itemView.findViewById(R.id.champion_splash)
        internal val nameView: TextView = itemView.findViewById(R.id.champion_name)
        internal val titleView: TextView = itemView.findViewById(R.id.champion_title)

    }

    companion object {
        private val TAG = ChampionAdapter::class.java.simpleName
    }
}
