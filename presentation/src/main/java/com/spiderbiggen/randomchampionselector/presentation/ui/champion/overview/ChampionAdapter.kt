package com.spiderbiggen.randomchampionselector.presentation.ui.champion.overview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.presentation.databinding.ItemChampionBinding
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.ChampionViewData

/**
 * Defines all behaviour for this [RecyclerView.Adapter].
 *
 * @author Stefan Breetveld
 */
class ChampionAdapter(private val clickListener: (Int) -> Unit) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val champions = mutableListOf<ChampionViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v = ItemChampionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v, clickListener)
    }

    override fun getItemId(position: Int): Long {
        return champions[position].id.toLong()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(champions[position])
    }

    override fun getItemCount(): Int = champions.size

    /**
     * Update the list of champions.
     * @param champions a list of [Champion] objects
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setChampions(champions: Collection<ChampionViewData>) {
        this.champions.clear()
        this.champions.addAll(champions)
        notifyDataSetChanged()
    }

    /**
     * [ViewHolder] for [ChampionAdapter] handles all changes to the ui when a different champion is set.
     *
     * @author Stefan Breetveld
     */
    class ViewHolder internal constructor(
        private val binding: ItemChampionBinding,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(viewData: ChampionViewData) = with(binding) {
            root.setOnClickListener { onClick(viewData.id) }
            championName.text = viewData.title
            championTitle.text = viewData.subtitle
            championSplash.setImageURI(viewData.image)
        }
    }
}
