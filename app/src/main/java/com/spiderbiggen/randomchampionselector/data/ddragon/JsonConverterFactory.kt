package com.spiderbiggen.randomchampionselector.data.ddragon

import android.util.JsonReader
import android.util.Log
import com.spiderbiggen.randomchampionselector.domain.Champion
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

class JsonConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if (type == LIST_CHAMPION) {
            return ChampionListConverter.INSTANCE
        } else if (type == STRING_ARRAY) {
            return StringArrayConverter.INSTANCE
        }
        return retrofit?.nextResponseBodyConverter<Any>(null, type!!, annotations!!)
    }

    internal class StringArrayConverter : Converter<ResponseBody, Array<String>> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Array<String> =
                JsonReader(value.charStream()).use { reader ->
                    reader.beginArray()
                    val strings = ArrayList<String>()
                    while (reader.hasNext()) {
                        strings.add(reader.nextString())
                    }
                    return strings.toTypedArray()
                }

        companion object {
            val INSTANCE = StringArrayConverter()
        }
    }

    internal class ChampionListConverter : Converter<ResponseBody, List<Champion>> {

        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): List<Champion> {
            try {
                val string = responseBody.string()
                Log.d(TAG, "convert: $string")
                val jsonObject = JSONObject(string).getJSONObject("data")
                val list = mutableListOf<Champion>()
                val stringIterator = jsonObject.keys()
                while (stringIterator.hasNext()) {
                    val key = stringIterator.next()
                    try {
                        val champion = Champion.parse(jsonObject.getJSONObject(key))
                        if (champion != null) {
                            list.add(champion)
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "convert: failed to convert $key", e)
                    }
                }
                return list
            } catch (e: JSONException) {
                throw IOException("Failed to parse Champions JSON", e)
            }

        }

        companion object {
            val INSTANCE = ChampionListConverter()
        }
    }

    companion object {

        private val TAG = JsonConverterFactory::class.java.simpleName

        private val LIST_CHAMPION = object : ParameterizedType {
            override fun getRawType(): Type {
                return List::class.javaObjectType
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(Champion::class.java)
            }
        }
        private val STRING_ARRAY = GenericArrayType { String::class.java }
    }
}
