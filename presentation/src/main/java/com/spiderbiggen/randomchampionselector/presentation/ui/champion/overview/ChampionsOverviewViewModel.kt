package com.spiderbiggen.randomchampionselector.presentation.ui.champion.overview

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.presentation.ui.champion.MapChampionViewData
import com.spiderbiggen.randomchampionselector.presentation.ui.common.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionsOverviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ChampionRepository,

    private val mapChampionViewData: MapChampionViewData,
) : ViewModel() {
    private val mutableState: MutableStateFlow<State<ChampionsOverviewViewData>> = MutableStateFlow(State.Loading())
    val state = mutableState.asStateFlow()

    private var splashChampionKey: Int?
        get() = savedStateHandle[CHAMPION_LOADED_KEY]
        set(value) {
            savedStateHandle[CHAMPION_LOADED_KEY] = value
        }

    fun handleViewCreated() = viewModelScope.launch {
        runCatching {
            val champions = repository.currentChampions()
            val random = splashChampionKey?.let { repository.getChampion(it) } ?: repository.randomChampion()!!
            splashChampionKey = random.key

            val viewData = ChampionsOverviewViewData(
                headerImage = mapChampionViewData(random).image,
                items = champions.map { mapChampionViewData(it) }
            )
            mutableState.value = State.Ready(viewData)
        }.onFailure {
            Log.e("ChampionsOverviewViewModel", it.message, it)
            mutableState.emit(State.Error(it))
        }
    }

    private companion object {
        private const val CHAMPION_LOADED_KEY = "champion_loaded_key"
    }
}