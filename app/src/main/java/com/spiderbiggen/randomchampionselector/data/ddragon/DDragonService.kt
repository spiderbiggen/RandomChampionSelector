package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import com.spiderbiggen.randomchampionselector.domain.Champion

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 *
 * @author Stefan Breetveld
 */
interface DDragonService {

    @get:GET("$API_URL/versions.json")
    val versions: Maybe<Array<String>>

    @GET("$CDN_URL/{version}/data/{locale}/championFull.json")
    fun getChampions(@Path("version") version: String, @Path("locale") locale: String): Maybe<List<Champion>>

    @GET("$CDN_URL/img/champion/splash/{champion}_{skin}.jpg")
    fun getSplashImage(@Path("champion") championKey: String, @Path("skin") skinId: Int): Maybe<Bitmap>

    companion object {
        const val API_URL = "/api"
        const val CDN_URL = "/cdn"
    }
}
