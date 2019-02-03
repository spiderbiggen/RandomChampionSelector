package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

/**
 * Custom converter class that has converters for Json -> List<Champion>, Json -> Array<String>, and ResponseBody -> Bitmaps.
 * Makes it easier to use get these types from retrofit requests.
 *
 * @author Stefan Breetveld
 */
class CustomConverter : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return when (type) {
            LIST_CHAMPION -> ChampionListConverter.INSTANCE
            STRING_ARRAY -> StringArrayConverter.INSTANCE
            BITMAP -> BitmapConverter.INSTANCE
            else -> retrofit?.nextResponseBodyConverter<Any>(null, type!!, annotations!!)
        }
    }

    private class StringArrayConverter : Converter<ResponseBody, Array<String>> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Array<String> {
            val reader = JsonReader(value.charStream())
            reader.beginArray()
            val strings = ArrayList<String>()
            while (reader.hasNext()) {
                strings.add(reader.nextString())
            }
            return strings.toTypedArray()
        }


        companion object {
            val INSTANCE
                get() = StringArrayConverter()
        }
    }

    private class ChampionListConverter : Converter<ResponseBody, List<Champion>> {

        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): List<Champion> {
            try {
                val string = responseBody.string()
                val jsonObject = JSONObject(string).getJSONObject("database")
                val list = mutableListOf<Champion>()
                val stringIterator = jsonObject.keys()
                while (stringIterator.hasNext()) {
                    val key = stringIterator.next()
                    try {
                        val champion = Champion.parse(jsonObject.getJSONObject(key))
                        list.add(champion)
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
            val INSTANCE
                get() = ChampionListConverter()
        }
    }

    private class BitmapConverter : Converter<ResponseBody, Bitmap> {

        override fun convert(responseBody: ResponseBody): Bitmap {
            return responseBody.byteStream().use { BitmapFactory.decodeStream(it) }
        }

        companion object {
            val INSTANCE
                get() = BitmapConverter()
        }
    }

    companion object {

        private val TAG = CustomConverter::class.java.simpleName

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
        private val BITMAP = Bitmap::class.java
    }
}
