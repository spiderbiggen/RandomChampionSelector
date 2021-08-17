package com.spiderbiggen.randomchampionselector.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback
import com.spiderbiggen.randomchampionselector.util.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.util.data.storage.repositories.ChampionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoaderViewModel(private val repository: ChampionRepository) : ViewModel() {
    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> = _finished

    fun verifyImages(progress: IProgressCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val champs = repository.allChampions.first()
            if (champs.isNullOrEmpty()) {
                updateData(progress)
            } else {
                val things = DDragon.verifyImages(champs, progress)
                Log.d("LoaderActivity", things.toString())
                DDragon.downloadAllImages(things, progress)
                _finished.postValue(true)
            }
        }
    }

    fun updateData(progress: IProgressCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            progress.update(IProgressCallback.Progress.CHECKING_VERSION)
            val version = DDragon.getLastVersion()
            val champions = DDragon.getChampionList(version)
            repository.addChampions(champions)
            val things = DDragon.verifyImages(champions, progress)
            if (!things.isEmpty()) {
                DDragon.downloadAllImages(things, progress)
            }
            _finished.postValue(true)
        }
    }

}

class LoaderViewModelFactory(private val repository: ChampionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoaderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}