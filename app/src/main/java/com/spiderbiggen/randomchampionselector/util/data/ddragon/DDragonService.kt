package com.spiderbiggen.randomchampionselector.util.data.ddragon

import android.graphics.Bitmap
import com.spiderbiggen.randomchampionselector.models.Champion

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the retrofit service which can request database from the DDragon static database api.
 *
 * @author Stefan Breetveld
 */
interface DDragonService {

    /**
     * Coroutine compliant request that retrieves the string array with all the versions.
     *
     * @return Deferred String array of versions
     */
    @GET("$API_URL/versions.json")
    suspend fun versions(): Array<String>

    /**
     * Coroutine compliant request that retrieves the champion roster for the given version and locale.
     *
     * @param version league of legends client version
     * @param version locale supplied by DDragon
     * @return deferred list of champions
     */
    @GET("$CDN_URL/{version}/data/{locale}/championFull.json")
    suspend fun getChampions(@Path("version") version: String, @Path("locale") locale: String): List<Champion>

    /**
     * Coroutine compliant request that retrieves the splash art for the given champion and skin.
     *
     * @param championKey [Champion.id] that identifies this champion.
     * @param skinId id of the skin. Valid ids depend on the champion.
     */
    @GET("$CDN_URL/img/champion/splash/{champion}_{skin}.jpg")
    suspend fun getSplashImage(@Path("champion") championKey: String, @Path("skin") skinId: Int): Bitmap

    companion object {
        private const val API_URL = "/api"
        private const val CDN_URL = "/cdn"
    }
}
