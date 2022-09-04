package com.spiderbiggen.randomchampionselector.data.champions

import com.spiderbiggen.randomchampionselector.data.champions.usecase.UpdateDataChampions
import com.spiderbiggen.randomchampionselector.domain.champions.usecase.UpdateChampions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ChampionsModule {
    @Provides
    fun provideUpdateChampions(provider: UpdateDataChampions): UpdateChampions = provider
}