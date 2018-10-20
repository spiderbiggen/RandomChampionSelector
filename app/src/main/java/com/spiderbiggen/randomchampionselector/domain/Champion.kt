package com.spiderbiggen.randomchampionselector.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*


/**
 * Class that defines the champion object.
 *
 * @author Stefan Breetveld
 */
@Entity
data class Champion(
        @PrimaryKey val key: Int,
        val id: String,
        val name: String,
        val title: String,
        val lore: String? = null,
        val blurb: String? = null,
        val roles: Array<String>? = null,
        @Embedded val info: Info? = null,
        @Embedded val image: Image? = null
) : Serializable {

    val capitalizedTitle: String
        get() = title.substring(0, 1).toUpperCase(Locale.ENGLISH) + title.substring(1)

    override fun toString(): String {
        return "Champion(key=$key, name='$name', roles=${Arrays.toString(roles)})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Champion) return false

        return key == other.key && id == other.id && name == other.name

    }

    override fun hashCode(): Int {
        var result = key
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object : Parsable<Champion> {
        @Throws(JSONException::class)
        override fun parse(jsonObject: JSONObject?): Champion? {
            if (jsonObject == null) return null
            val key = jsonObject.getInt("key")
            val id = jsonObject.getString("id")
            val blurb = jsonObject.optString("blurb")
            val lore = jsonObject.optString("lore")
            val title = jsonObject.optString("title")
            val name = jsonObject.optString("name")
            val roles = jsonArrayToStringArray(jsonObject.optJSONArray("tags"))
            val info = Info.parse(jsonObject.getJSONObject("info"))
            val image = Image.parse(jsonObject.getJSONObject("image"))
            return Champion(key, id, name, title, lore, blurb, roles, info, image)
        }

        private fun jsonArrayToStringArray(jsonArray: JSONArray?): Array<String>? {
            if (jsonArray == null) return null
            val tags = (0 until jsonArray.length()).map { jsonArray.getString(it) }
            return if (tags.isEmpty()) null else tags.toTypedArray()
        }
    }

    data class Info(val attack: Byte, val defense: Byte, val magic: Byte, val difficulty: Byte) : Serializable {

        companion object : Parsable<Info> {

            @Throws(JSONException::class)
            override fun parse(jsonObject: JSONObject?): Info? {
                if (jsonObject == null) return null
                val attack = jsonObject.optInt("attack", 0).toByte()
                val defense = jsonObject.optInt("defense", 0).toByte()
                val magic = jsonObject.optInt("magic", 0).toByte()
                val difficulty = jsonObject.optInt("difficulty", 0).toByte()
                return Info(attack, defense, magic, difficulty)
            }
        }

    }

    data class Image(val full: String, val sprite: String, val group: String, val x: Int = 0, val y: Int = 0, val w: Int = 0, var h: Int = 0) : Serializable {

        companion object : Parsable<Image> {

            @Throws(JSONException::class)
            override fun parse(jsonObject: JSONObject?): Image? {
                if (jsonObject == null) return null
                val full = jsonObject.getString("full")
                val sprite = jsonObject.getString("sprite")
                val group = jsonObject.getString("group")
                val x = jsonObject.optInt("x", 0)
                val y = jsonObject.optInt("y", 0)
                val w = jsonObject.optInt("w", 0)
                val h = jsonObject.optInt("x", 0)
                return Image(full, sprite, group, x, y, w, h)
            }
        }

    }
}
