package com.spiderbiggen.randomchampionselector.model

import org.json.JSONException
import org.json.JSONObject

/**
 * Created on 1-7-2018.
 *
 * @author Stefan Breetveld
 */
interface Parsable<T> {

    @Throws(JSONException::class)
    fun parse(jsonObject: JSONObject?): T?
}
