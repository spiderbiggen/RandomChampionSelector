package com.spiderbiggen.randomchampionselector.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.presentation.cache.BitmapCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionViewModel @Inject constructor(
    private val repository: ChampionRepository,
    private val bitmapCache: BitmapCache,
) : ViewModel() {
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
        if (champion == null) return null
        val bitmap: Deferred<Bitmap?> = viewModelScope.async(Dispatchers.IO) {
            bitmapCache.loadBitmap(champion).getOrNull()
        }
        return bitmap.await()
    }
}
