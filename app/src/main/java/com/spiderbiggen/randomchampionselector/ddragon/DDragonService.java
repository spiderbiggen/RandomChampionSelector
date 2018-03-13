package com.spiderbiggen.randomchampionselector.ddragon;

import com.spiderbiggen.randomchampionselector.model.Champion;

import java.util.List;

import io.reactivex.Maybe;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created on 13-3-2018.
 *
 * @author Stefan Breetveld
 */

public interface DDragonService {

    String API_URL = "/api";
    String CDN_URL = "/cdn";

    @GET(API_URL + "/versions.json")
    Maybe<String[]> getVersions();

    @GET(CDN_URL + "/{version}/data/{locale}/championFull.json")
    Maybe<List<Champion>> getChampions(@Path("version") String version, @Path("locale") String locale);

    @GET(CDN_URL + "/img/champion/splash/{champion}_{skin}.jpg")
    Maybe<ResponseBody> getSplashImage(@Path("champion") String championKey, @Path("skin") int skinId);

    @GET(CDN_URL + "/{version}/img/champion/{champion}.png")
    Maybe<ResponseBody> getSquareImage(@Path("version") String version, @Path("champion") String championKey);
}
