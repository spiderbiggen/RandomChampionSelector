package com.spiderbiggen.randomchampionselector.models

import org.json.JSONException
import org.json.JSONObject

/**
 * Tells other object that this object can convert a [JSONObject] to [T]
 *
 * @author Stefan Breetveld
 */
interface Parsable<T> {

    /**
     * Parse [jsonObject] and convert to [T]
     * @param jsonObject a jsonObject
     * @return [T] filled with data from [jsonObject]
     *
     * @throws JSONException if there are errors in the json syntax or when parts of [T] are missing in [jsonObject]
     */
    @Throws(JSONException::class)
    fun parse(jsonObject: JSONObject): T
}
