package com.spiderbiggen.randomchampionselector.data.storage.database.converters

import android.text.TextUtils
import androidx.room.TypeConverter

/**
 * Converter for room to be able to store String arrays
 *
 * @author Stefan Breetveld
 */
class StringListConverter {

    /**
     * Converts a string to a string array split on [DELIMITER]
     *
     * @param value the string that should be converted to an array
     * @return [value] split into multiple strings
     */
    @TypeConverter
    fun toStringArray(value: String): List<String> = value.split(DELIMITER).filterNot(String::isEmpty)


    /**
     * Converts a string array to a string joined by [DELIMITER]
     *
     * @param values the strings that should be joined
     * @return [values] joined together into one string
     */
    @TypeConverter
    fun toString(values: List<String>): String = TextUtils.join(DELIMITER, values)

    companion object {
        private const val DELIMITER = ","
    }
}
