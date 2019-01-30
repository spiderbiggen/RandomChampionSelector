package com.spiderbiggen.randomchampionselector.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.data.onMainThread
import com.spiderbiggen.randomchampionselector.domain.Champion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class ChampionAdapter(champions: Collection<Champion>, private val clickListener: View.OnClickListener) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>(){

    private val champions = champions.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_champion_item, parent, false)
        v.setOnClickListener(clickListener)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.champion = champions[position]
    }

    override fun getItemCount(): Int = champions.size

    fun setChampions(champions: Collection<Champion>) {
        this.champions.clear()
        this.champions.addAll(champions)
        notifyDataSetChanged()
    }

    fun getChampion(position: Int): Champion? = champions.getOrNull(position)

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), CoroutineScope by CoroutineScope(Dispatchers.Default) {
        private val imageView: ImageView = itemView.findViewById(R.id.champion_splash)

        private val nameView: TextView = itemView.findViewById(R.id.champion_name)
        private val titleView: TextView = itemView.findViewById(R.id.champion_title)

        internal var champion: Champion? = null
            set(value) {
                if (value == null) throw NullPointerException()
                updateImage(value)
                nameView.text = value.name
                titleView.text = value.capitalizedTitle
                field = value
            }

        private fun updateImage(champion: Champion) {
            launch {
                try {
                    val bitmap = BitmapCache.loadBitmap(champion)
                    onMainThread { imageView.setImageBitmap(bitmap) }
                } catch (e: IOException) {
                    onMainThread { loadImageFailure(e.message) }
                }
            }
        }

        private fun loadImageFailure(message: String?) {
            imageView.setImageBitmap(null)
            Log.e("ChampionAdapter", "error $message")
        }
    }
}
