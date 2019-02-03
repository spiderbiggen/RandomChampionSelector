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
        val lore: String,
        val blurb: String,
        val roles: Array<String>,
        @Embedded val info: Info
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
        override fun parse(jsonObject: JSONObject): Champion {
            val key = jsonObject.getInt("key")
            val id = jsonObject.getString("id")
            val blurb = jsonObject.optString("blurb")
            val lore = jsonObject.optString("lore")
            val title = jsonObject.optString("title")
            val name = jsonObject.optString("name")
            val roles = jsonArrayToStringArray(jsonObject.optJSONArray("tags"))
            val info = Info.parse(jsonObject.getJSONObject("info"))
            return Champion(key, id, name, title, lore, blurb, roles, info)
        }

        private fun jsonArrayToStringArray(jsonArray: JSONArray): Array<String> {
            val tags = (0 until jsonArray.length()).map(jsonArray::getString)
            return tags.toTypedArray()
        }
    }

    data class Info(val attack: Byte, val defense: Byte, val magic: Byte, val difficulty: Byte) : Serializable {
        companion object : Parsable<Info> {
            @Throws(JSONException::class)
            override fun parse(jsonObject: JSONObject): Info {
                val attack = jsonObject.optInt("attack", 0).toByte()
                val defense = jsonObject.optInt("defense", 0).toByte()
                val magic = jsonObject.optInt("magic", 0).toByte()
                val difficulty = jsonObject.optInt("difficulty", 0).toByte()
                return Info(attack, defense, magic, difficulty)
            }
        }

    }
}
