package com.spiderbiggen.randomchampionselector.data.storage.database.converters

import android.arch.persistence.room.TypeConverter
import android.text.TextUtils

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */

class StringArrayConverter {

    @TypeConverter
    fun toStringArray(value: String?): Array<String>? {
        return value?.split(DELIMITER)?.dropLastWhile { it.isEmpty() }?.toTypedArray()
    }

    @TypeConverter
    fun toString(values: Array<String>?): String? {
        return if (values == null) null else TextUtils.join(DELIMITER, values)
    }

    companion object {
        private const val DELIMITER = ","
    }
}
