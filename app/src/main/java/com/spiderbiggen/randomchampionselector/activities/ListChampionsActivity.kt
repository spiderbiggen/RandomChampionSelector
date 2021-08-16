package com.spiderbiggen.randomchampionselector.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.adapters.ChampionAdapter
import com.spiderbiggen.randomchampionselector.databinding.ActivityListChampionsBinding
import com.spiderbiggen.randomchampionselector.interfaces.Contextual
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.cache.BitmapCache
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
class ListChampionsActivity : AbstractActivity() {
    private lateinit var binding: ActivityListChampionsBinding

    private val adapter = ChampionAdapter(View.OnClickListener(this::onClick))
    private lateinit var viewModel: ChampionListViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChampionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
        binding.championList.adapter = adapter
        binding.activity = this
        viewModel = ViewModelProvider(this).get(ChampionListViewModel::class.java)
        viewModel.useContext(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onResume() {
        viewModel.getChampions().observe(this, { adapter.setChampions(it) })
        viewModel.getHeaderImage().observe(this, { binding.splash.setImageBitmap(it) })
        super.onResume()
    }

    fun openChampion(view: View) {
        openChampionActivity(view)
    }

    private fun onClick(v: View) {
        val position = binding.championList.getChildAdapterPosition(v)
        val img = v.findViewById<ImageView>(R.id.champion_splash)
        val name = v.findViewById<TextView>(R.id.champion_name)
        val title = v.findViewById<TextView>(R.id.champion_title)
        val champion = adapter.getChampion(position)
        val intent = Intent(this, ChampionActivity::class.java)
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion?.key)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair(img, getString(R.string.champion_splash_transition_key)),
            Pair(name, getString(R.string.champion_name_transition_key)),
            Pair(title, getString(R.string.champion_title_transition_key))
        )
        intent.putExtra(ChampionActivity.UP_ON_BACK_KEY, false)
        startActivityWithFade(intent, options.toBundle())
    }

    class ChampionListViewModel : ViewModel(), Contextual,
            CoroutineScope by CoroutineScope(Dispatchers.Default) {
        private lateinit var champions: MutableLiveData<List<Champion>>

        private lateinit var bitmap: MutableLiveData<Bitmap>
        private lateinit var context: WeakReference<Context>

        override fun useContext(context: Context) {
            this.context = WeakReference(context)
        }


        fun getHeaderImage(): MutableLiveData<Bitmap> {
            if (!::bitmap.isInitialized) {
                bitmap = MutableLiveData()
                findChampion()
            }
            return bitmap
        }

        private fun findChampion() {
            launch {
                do {
                    val champion = context.get()?.database?.findRandomChampion() ?: continue
                    try {
                        bitmap.postValue(BitmapCache.loadBitmap(champion))
                    } catch (e: IOException) {
                        // Catch errors and try again
                    }
                } while (!::bitmap.isInitialized)
            }
        }

        fun getChampions(): MutableLiveData<List<Champion>> {
            if (!::champions.isInitialized) {
                champions = MutableLiveData()
                launch {
                    champions.postValue(context.get()?.database?.findChampionList())
                }
            }
            return champions
        }

        override fun onCleared() {
            cancel()
        }
    }
}
