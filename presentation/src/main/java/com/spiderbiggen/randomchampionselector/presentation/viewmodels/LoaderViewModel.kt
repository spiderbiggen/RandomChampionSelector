package com.spiderbiggen.randomchampionselector.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.domain.champions.usecase.UpdateChampions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderViewModel @Inject constructor(
    private val updateChampions: dagger.Lazy<UpdateChampions>
) : ViewModel() {
    private val mutableState = MutableLiveData<DownloadProgress>(DownloadProgress.Idle)
    val state: LiveData<DownloadProgress> = mutableState

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun loadData(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateChampions.get().update(forceRefresh)
                .collect { mutableState.postValue(it) }
        } catch (t: Throwable) {
            Log.e("LoaderViewModel", t.message, t)
        }
    }
}
