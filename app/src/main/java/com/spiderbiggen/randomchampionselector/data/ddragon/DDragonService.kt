package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import com.spiderbiggen.randomchampionselector.domain.Champion
import kotlinx.coroutines.Deferred

import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 *
 * @author Stefan Breetveld
 */
interface DDragonService {

    @GET("$API_URL/versions.json")
    fun versions(): Deferred<Array<String>>

    @GET("$CDN_URL/{version}/data/{locale}/championFull.json")
    fun getChampions(@Path("version") version: String, @Path("locale") locale: String): Deferred<List<Champion>>

    @GET("$CDN_URL/img/champion/splash/{champion}_{skin}.jpg")
    fun getSplashImage(@Path("champion") championKey: String, @Path("skin") skinId: Int): Deferred<Bitmap>

    companion object {
        const val API_URL = "/api"
        const val CDN_URL = "/cdn"
    }
}
