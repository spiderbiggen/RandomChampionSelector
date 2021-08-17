package com.spiderbiggen.randomchampionselector.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.util.data.storage.repositories.ChampionRepository
import kotlinx.coroutines.*
import java.io.IOException

class ChampionViewModel(private val repository: ChampionRepository) : ViewModel() {
    private val _champion: MutableLiveData<Champion> = MutableLiveData()
    val champion: LiveData<Champion> = _champion
    val bitmap: LiveData<Bitmap?> by lazy {
        champion.switchMap {
            liveData {
                emit(getBitmap(it))
            }
        }
    }

    fun setChampion(id: Int) {
        viewModelScope.launch {
            when {
                id < 0 -> repository.randomChampion()
                else -> repository.getChampion(id)
            }?.let {
                _champion.postValue(it)
            }

        }
    }

    private suspend fun getBitmap(champion: Champion?): Bitmap? {
        if(champion == null) return null
        val bitmap: Deferred<Bitmap?> = viewModelScope.async(Dispatchers.IO) {
            try {
                return@async BitmapCache.loadBitmap(champion)
            } catch (e: IOException) {
                // Catch errors and try again
            }
            return@async null
        }
        return bitmap.await()
    }
}

class ChampionViewModelFactory(private val repository: ChampionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChampionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChampionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}