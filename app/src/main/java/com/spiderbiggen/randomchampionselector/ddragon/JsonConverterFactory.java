package com.spiderbiggen.randomchampionselector.ddragon;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.spiderbiggen.randomchampionselector.model.Champion;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created on 1-7-2018.
 *
 * @author Stefan Breetveld
 */
public class JsonConverterFactory extends Converter.Factory {

    private static final String TAG = JsonConverterFactory.class.getSimpleName();

    private static final Type LIST_CHAMPION = new ParameterizedType() {
        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }

        public Type[] getActualTypeArguments() {
            return new Type[] {Champion.class};
        }
    };
    private static final Type STRING_ARRAY = new GenericArrayType() {
        @Override
        public Type getGenericComponentType() {
            return String.class;
        }
    };

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type.equals(LIST_CHAMPION)) {
            return ChampionListConverter.INSTANCE;
        } else if (type.equals(STRING_ARRAY)) {
            return StringArrayConverter.INSTANCE;
        }
        return retrofit.nextResponseBodyConverter(null, type, annotations);
    }

    static final class StringArrayConverter implements Converter<ResponseBody, String[]> {

        static final StringArrayConverter INSTANCE = new StringArrayConverter();

        @Override
        public String[] convert(ResponseBody value) throws IOException {
            try (JsonReader reader = new JsonReader(value.charStream())) {
                reader.beginArray();
                List<String> strings = new ArrayList<>();
                while (reader.hasNext()) {
                    strings.add(reader.nextString());
                }
                String[] arr = new String[strings.size()];
                strings.toArray(arr);
                return arr;
            }
        }
    }

    static final class ChampionListConverter implements Converter<ResponseBody, List<Champion>> {

        static final ChampionListConverter INSTANCE = new ChampionListConverter();

        @Override
        public List<Champion> convert(ResponseBody responseBody) throws IOException {
            try {
                String string = responseBody.string();
                Log.d(TAG, "convert: " + string);
                JSONObject object = new JSONObject(string);
                object = object.getJSONObject("data");
                List<Champion> list = new ArrayList<>();
                Iterator<String> stringIterator = object.keys();
                while (stringIterator.hasNext()) {
                    String key = stringIterator.next();
                    try {
                        Champion champion = new Champion();
                        list.add(champion.parse(object.getJSONObject(key)));
                    } catch (JSONException e) {
                        Log.e(TAG, "convert: failed to convert " + key, e);
                    }
                }
                return list;
            } catch (JSONException e) {
                throw new IOException("Failed to parse Champions JSON", e);
            }
        }
    }
}
