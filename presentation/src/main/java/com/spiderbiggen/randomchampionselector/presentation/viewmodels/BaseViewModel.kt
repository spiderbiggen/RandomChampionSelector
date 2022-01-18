package com.spiderbiggen.randomchampionselector.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val fileRepository: FileRepository,
) : ViewModel() {
    fun clearImages() {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepository.deleteChampionImages()
        }
    }
}