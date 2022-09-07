package com.spiderbiggen.randomchampionselector.data.ddragon.models

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

data class ApiChampion(
    val key: Int,
    val id: String,
    val name: String,
    val title: String,
    val lore: String,
    val blurb: String,
    val roles: Array<String>,
    val info: ApiInfo
) {
    override fun toString(): String {
        return "Champion(key=$key, name='$name', roles=${roles.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiChampion) return false

        return key == other.key && id == other.id && name == other.name
    }

    override fun hashCode(): Int {
        var result = key
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object : Parsable<ApiChampion> {
        override fun parse(jsonObject: JSONObject): ApiChampion {
            val key = jsonObject.getInt("key")
            val id = jsonObject.getString("id")
            val blurb = jsonObject.optString("blurb")
            val lore = jsonObject.optString("lore")
            val title = jsonObject.optString("title")
            val name = jsonObject.optString("name")
            val roles = jsonArrayToStringArray(jsonObject.optJSONArray("tags") ?: JSONArray())
            val info = ApiInfo.parse(jsonObject.getJSONObject("info"))
            return ApiChampion(key, id, name, title, lore, blurb, roles, info)
        }

        private fun jsonArrayToStringArray(jsonArray: JSONArray): Array<String> {
            val tags = (0 until jsonArray.length()).map(jsonArray::getString)
            return tags.toTypedArray()
        }
    }
}