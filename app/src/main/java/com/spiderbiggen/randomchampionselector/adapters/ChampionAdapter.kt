package com.spiderbiggen.randomchampionselector.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.util.data.onMainThread
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Defines all behaviour for this [RecyclerView.Adapter].
 *
 * @author Stefan Breetveld
 */
class ChampionAdapter(
    private val clickListener: View.OnClickListener,
    champions: Collection<Champion> = listOf()
) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>() {

    private val champions = champions.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_champion_item, parent, false)
        v.setOnClickListener(clickListener)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.champion = champions[position]
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.cancel()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = champions.size

    /**
     * Update the list of champions.
     * @param champions a list of [Champion] objects
     */
    fun setChampions(champions: Collection<Champion>) {
        this.champions.clear()
        this.champions.addAll(champions)
        notifyDataSetChanged()
    }

    /**
     * Retrieve the champion stored at that position in the list.
     *
     * @return the requested champion or null if the given index doesn't exist
     */
    fun getChampion(position: Int): Champion? = champions.getOrNull(position)

    /**
     * [ViewHolder] for [ChampionAdapter] handles all changes to the ui when a different champion is set.
     *
     * @author Stefan Breetveld
     */
    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        CoroutineScope by CoroutineScope(Dispatchers.Default) {

        private val imageView: ImageView = itemView.findViewById(R.id.champion_splash)
        private val nameView: TextView = itemView.findViewById(R.id.champion_name)
        private val titleView: TextView = itemView.findViewById(R.id.champion_title)
        private var a: Job? = null

        internal var champion: Champion? = null
            set(value) {
                if (value == null) throw NullPointerException()
                a?.cancel()
                updateImage(value)
                nameView.text = value.name
                titleView.text = value.capitalizedTitle
                field = value
            }

        private fun updateImage(champion: Champion) {
            a = launch {
                val bitmap = try {
                    BitmapCache.loadBitmap(champion)
                } catch (e: IOException) {
                    Log.e("ChampionAdapter", "error ${e.message}")
                    null
                }
                onMainThread { imageView.setImageBitmap(bitmap) }
            }
        }
    }
}
