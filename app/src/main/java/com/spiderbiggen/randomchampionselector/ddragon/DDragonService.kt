package com.spiderbiggen.randomchampionselector.ddragon

import com.spiderbiggen.randomchampionselector.model.Champion

import io.reactivex.Maybe
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface DDragonService {

    @get:GET("$API_URL/versions.json")
    val versions: Maybe<Array<String>>

    @GET("$CDN_URL/{version}/data/{locale}/championFull.json")
    fun getChampions(@Path("version") version: String, @Path("locale") locale: String): Maybe<List<Champion>>

    @GET("$CDN_URL/img/champion/splash/{champion}_{skin}.jpg")
    fun getSplashImage(@Path("champion") championKey: String, @Path("skin") skinId: Int): Maybe<ResponseBody>

    @GET("$CDN_URL/{version}/img/champion/{champion}.png")
    fun getSquareImage(@Path("version") version: String, @Path("champion") championKey: String): Maybe<ResponseBody>

    companion object {
        const val API_URL = "/api"
        const val CDN_URL = "/cdn"
    }
}
