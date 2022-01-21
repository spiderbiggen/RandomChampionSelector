package com.spiderbiggen.randomchampionselector.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.presentation.cache.BitmapCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionListViewModel @Inject constructor(
    private val repository: ChampionRepository,
    private val bitmapCache: BitmapCache,
) : ViewModel() {
    val champions: LiveData<List<Champion>> = repository.champions.asLiveData()
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
            bitmapCache.loadBitmap(champion).getOrNull()
        }
        return bitmap.await()
    }
}