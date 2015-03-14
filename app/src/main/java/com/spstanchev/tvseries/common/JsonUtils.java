package com.spstanchev.tvseries.common;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Stefan on 2/21/2015.
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    public static String getJsonString(JSONObject currentJsonObject, String tag) {
        if (currentJsonObject != null) {
            try {
                return currentJsonObject.getString(tag);
            } catch (JSONException e) {
                Log.v(TAG, "Caught JsonException when trying to get JSONObject with name " + tag + " from " + currentJsonObject.toString());
            }
        }
        return "";
    }

    public static int getJsonInteger(JSONObject currentJsonObject, String tag) {
        if (currentJsonObject != null) {
            try {
                return currentJsonObject.getInt(tag);
            } catch (JSONException e) {
                Log.v(TAG, "Caught JsonException when trying to get JSONObject with name " + tag + " from " + currentJsonObject.toString());
            }
        }
        return -1;
    }

    public static double getJsonDouble(JSONObject currentJsonObject, String tag) {
        if (currentJsonObject != null) {
            try {
                return currentJsonObject.getDouble(tag);
            } catch (JSONException e) {
                Log.v(TAG, "Caught JsonException when trying to get JSONObject with name " + tag + " from " + currentJsonObject.toString());
            }
        }
        return 0;
    }

    public static JSONObject getJSONObjectSafely(JSONObject parentJsonObject, String tag) {
        if (parentJsonObject != null) {
            try {
                return parentJsonObject.getJSONObject(tag);
            } catch (JSONException e) {
                Log.v(TAG, "Caught JsonException when trying to get JSONObject with name " + tag + " from " + parentJsonObject.toString());
            }
        }
        return null;
    }
}
