package com.spiderbiggen.randomchampionselector.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.coroutines.onMainThread
import com.spiderbiggen.randomchampionselector.presentation.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.presentation.databinding.ItemChampionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Defines all behaviour for this [RecyclerView.Adapter].
 *
 * @author Stefan Breetveld
 */
class ChampionAdapter(
    private val clickListener: View.OnClickListener,
    private val bitmapCache: BitmapCache,
    champions: Collection<Champion> = listOf()
) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val champions = champions.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = ItemChampionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        v.root.setOnClickListener(clickListener)
        return ViewHolder(v, bitmapCache)
    }

    override fun getItemId(position: Int): Long {
        return champions[position].key.toLong()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.champion = champions[position]
    }

    override fun getItemCount(): Int = champions.size

    /**
     * Update the list of champions.
     * @param champions a list of [Champion] objects
     */
    @SuppressLint("NotifyDataSetChanged")
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
    class ViewHolder internal constructor(private val binding: ItemChampionBinding, private val cache: BitmapCache) :
        RecyclerView.ViewHolder(binding.root),
        CoroutineScope by CoroutineScope(Dispatchers.Default) {

        private var job: Job? = null

        internal var champion: Champion? = null
            set(value) {
                if (value == null) throw NullPointerException()
                job?.cancel()
                updateImage(value)
                binding.championName.text = value.name
                binding.championTitle.text = value.capitalizedTitle
                field = value
            }

        private fun updateImage(champion: Champion) {
            job = launch(Dispatchers.IO) {
                val bitmap = cache.loadBitmap(champion)
                    .onFailure { Log.e("ChampionAdapter", "error ${it.message}") }
                    .getOrNull()
                onMainThread { binding.championSplash.setImageBitmap(bitmap) }
            }
        }
    }
}
