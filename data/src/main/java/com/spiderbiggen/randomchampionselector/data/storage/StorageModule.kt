package com.spiderbiggen.randomchampionselector.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.spiderbiggen.randomchampionselector.data.storage.database.SimpleDatabase
import com.spiderbiggen.randomchampionselector.data.storage.database.SimpleDatabaseDecorator
import com.spiderbiggen.randomchampionselector.data.storage.database.daos.ChampionDAO
import com.spiderbiggen.randomchampionselector.data.storage.file.FileDataRepository
import com.spiderbiggen.randomchampionselector.data.storage.repositories.ChampionDataRepository
import com.spiderbiggen.randomchampionselector.data.storage.repositories.PreferenceDataRepository
import com.spiderbiggen.randomchampionselector.domain.champions.repository.ChampionRepository
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {
    @Provides
    fun provideFileStorage(@ApplicationContext context: Context): FileRepository =
        FileDataRepository(context.filesDir)

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun providePreferenceRepository(provider: PreferenceDataRepository): PreferenceRepository = provider

    @Provides
    fun provideChampionRepository(provider: ChampionDataRepository): ChampionRepository = provider

    @Provides
    @Singleton
    fun providePostDatabase(@ApplicationContext context: Context): SimpleDatabaseDecorator {
        val room = Room.databaseBuilder(context, SimpleDatabase::class.java, "random_champion_main")
            .fallbackToDestructiveMigration()
            .build()
        return SimpleDatabaseDecorator(room)
    }

    @Provides
    fun provideChampionDao(decorator: SimpleDatabaseDecorator): ChampionDAO = decorator.championDao()
}