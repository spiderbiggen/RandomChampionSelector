package com.spiderbiggen.randomchampionselector.data.ddragon.models

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

data class ApiInfo(val attack: Byte, val defense: Byte, val magic: Byte, val difficulty: Byte): Serializable {
    companion object : Parsable<ApiInfo> {
        @Throws(JSONException::class)
        override fun parse(jsonObject: JSONObject): ApiInfo {
            val attack = jsonObject.optInt("attack", 0).toByte()
            val defense = jsonObject.optInt("defense", 0).toByte()
            val magic = jsonObject.optInt("magic", 0).toByte()
            val difficulty = jsonObject.optInt("difficulty", 0).toByte()
            return ApiInfo(attack, defense, magic, difficulty)
        }
    }

}