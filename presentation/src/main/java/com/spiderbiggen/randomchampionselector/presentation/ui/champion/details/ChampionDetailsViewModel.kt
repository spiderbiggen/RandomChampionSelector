package com.spiderbiggen.randomchampionselector.presentation.ui.champion.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.ChampionViewData
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.MapChampionViewData
import com.spiderbiggen.randomchampionselector.presentation.ui.common.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class ChampionDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // Repository
    private val deferredChampionRepository: Provider<ChampionRepository>,
    private val mapChampionViewData: MapChampionViewData,
) : ViewModel() {

    private val repository by lazy { deferredChampionRepository.get() }
    private val args = ChampionDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val mutableState: MutableStateFlow<State<ChampionViewData>> = MutableStateFlow(State.Loading())
    val state = mutableState.asStateFlow()

    fun handleViewCreated() = viewModelScope.launch {
        runCatching {
            val championKey = savedStateHandle[CHAMPION_LOADED_KEY] ?: args.championKey.takeIf { it >= 0 }
            val champion = championKey?.let { repository.getChampion(it) } ?: repository.randomChampion()!!
            savedStateHandle[CHAMPION_LOADED_KEY] = champion.key
            mutableState.emit(State.Ready(mapChampionViewData(champion)))
        }.onFailure {
            mutableState.emit(State.Error(it))
        }
    }


    private companion object {
        private const val CHAMPION_LOADED_KEY = "loaded_champion_key"
    }
}
