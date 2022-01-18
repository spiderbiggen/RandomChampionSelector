package com.spiderbiggen.randomchampionselector.data.ddragon

import com.spiderbiggen.randomchampionselector.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideRetrofit():DDragonService {
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder().baseUrl(DDragon.BASE_URL).client(httpClient.build())
            .addConverterFactory(CustomConverter())
            .build()
        return retrofit.create(DDragonService::class.java)
    }
}