package com.spiderbiggen.randomchampionselector.presentation.ui.champion.overview

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.presentation.databinding.ItemChampionBinding
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.ChampionViewData

/**
 * Defines all behaviour for this [RecyclerView.Adapter].
 *
 * @author Stefan Breetveld
 */
class ChampionAdapter(
    private val clickListener: (Int) -> Unit,
    private val fullRequest: RequestBuilder<Drawable>,
    private val thumbRequest: RequestBuilder<Drawable>
) : RecyclerView.Adapter<ChampionAdapter.ViewHolder>(),
    ListPreloader.PreloadModelProvider<Uri> {

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
     * @param newChampions a new list of [Champion] objects
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setChampions(newChampions: Collection<ChampionViewData>) {
        champions.apply {
            clear()
            addAll(newChampions)
        }
        notifyDataSetChanged()
    }

    /**
     * [ViewHolder] for [ChampionAdapter] handles all changes to the ui when a different champion is set.
     *
     * @author Stefan Breetveld
     */
    data class ViewHolder internal constructor(
        internal val binding: ItemChampionBinding,
        internal val onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root)

    private fun ViewHolder.bind(viewData: ChampionViewData): Unit = with(binding) {
        root.setOnClickListener { onClick(viewData.id) }
        championName.text = viewData.title
        championTitle.text = viewData.subtitle
        fullRequest.thumbnail(thumbRequest.load(viewData.image)).load(viewData.image).into(championSplash)
    }

    override fun getPreloadItems(position: Int): List<Uri> =
        listOfNotNull(champions[position].image)

    override fun getPreloadRequestBuilder(item: Uri): RequestBuilder<Drawable> =
        fullRequest.thumbnail(thumbRequest.load(item)).load(item)
}
