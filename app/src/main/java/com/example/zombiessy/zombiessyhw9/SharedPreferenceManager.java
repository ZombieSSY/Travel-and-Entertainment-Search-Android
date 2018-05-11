package com.example.zombiessy.zombiessyhw9;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class SharedPreferenceManager {
    // Shared preferences file name
    private static final String PREF_NAME = "FavouriteList";
    // LogCat tag
    private static String TAG = SharedPreferenceManager.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static SharedPreferenceManager instance;

    // Shared pref mode
    int PRIVATE_MODE = Context.MODE_PRIVATE;

    public static SharedPreferenceManager getInstance(Context context) {
        return instance == null ? new SharedPreferenceManager(context) : instance;
    }

    @SuppressLint("CommitPrefEdits")
    public SharedPreferenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFavourite(String placeId, String json_string) {
        editor.putString(placeId, json_string);
        editor.commit();
        Log.d(TAG, "User set favourites");
    }

    public boolean isFavourite(String placeId) {
        return pref.contains(placeId);
    }

    public void removeFavourite(String placeId) {
        editor.remove(placeId);
        editor.commit();
        Log.d(TAG, "User remove favourites");
    }

    public Map<String, ?> getAll() {
        return pref.getAll();
    }

    public String getFavourite(String placeId) {
        return pref.getString(placeId, "-1");
    }


}
