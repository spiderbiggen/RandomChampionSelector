package com.spiderbiggen.randomchampionselector.presentation.ui.splash

import android.util.Log
import androidx.lifecycle.*
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.domain.champions.usecase.UpdateChampions
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fileRepository: FileRepository,
    private val updateChampions: dagger.Lazy<UpdateChampions>
) : ViewModel() {

    private val args = SplashFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val mutableState = MutableLiveData<DownloadProgress>(DownloadProgress.Idle)
    val state: LiveData<DownloadProgress> = mutableState

    fun handleViewCreated() = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            if (args.clearImages) {
                fileRepository.deleteChampionImages()
            }
            updateChampions.get().update(args.forceRefresh)
                .collect { mutableState.postValue(it) }
        }.onFailure {
            Log.e("LoaderViewModel", it.message, it)
        }
    }
}
