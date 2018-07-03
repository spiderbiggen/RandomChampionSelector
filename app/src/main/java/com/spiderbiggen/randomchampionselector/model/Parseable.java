package com.spiderbiggen.randomchampionselector.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 1-7-2018.
 *
 * @author Stefan Breetveld
 */
public interface Parseable<T> {

    public T parse(JSONObject object) throws JSONException;
}
