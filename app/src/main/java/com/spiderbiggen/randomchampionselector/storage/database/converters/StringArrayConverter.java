package com.spiderbiggen.randomchampionselector.storage.database.converters;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */

public class StringArrayConverter {

    private static final String DELIMITER = ",";

    @TypeConverter
    public static String[] toStringArray(String value) {
        return value == null ? null : value.split(DELIMITER);
    }

    @TypeConverter
    public static String toString(String[] values) {
        return values == null ? null : TextUtils.join(DELIMITER, values);
    }
}
