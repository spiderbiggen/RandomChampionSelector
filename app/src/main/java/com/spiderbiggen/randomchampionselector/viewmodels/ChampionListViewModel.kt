package com.spiderbiggen.randomchampionselector.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.util.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.util.data.storage.repositories.ChampionRepository
import kotlinx.coroutines.*
import java.io.IOException

class ChampionListViewModel(private val repository: ChampionRepository) : ViewModel() {
    val champions: LiveData<List<Champion>> = repository.allChampions.asLiveData()
    private val _champion = MutableLiveData<Champion>()
    val bitmap: LiveData<Bitmap?> = _champion.switchMap {
        liveData {
            emit(getBitmap(it))
        }
    }

    fun selectRandomChampion() {
        Log.d("ChampionVM", "New Random")
        viewModelScope.launch {
            repository.randomChampion()?.let {
                _champion.postValue(it)
            }

        }
    }

    private suspend fun getBitmap(champion: Champion?): Bitmap? {
        if (champion == null) return null
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

class ChampionListViewModelFactory(private val repository: ChampionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChampionListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChampionListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}