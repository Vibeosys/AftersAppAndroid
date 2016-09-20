package com.aftersapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by akshay on 19-09-2016.
 */
public class SessionManager {

    private static final String PROJECT_PREFERENCES = "com.aftersapp";


    private static SessionManager mSessionManager;
    private static SharedPreferences mProjectSharedPref = null;
    private static Context mContext = null;
    private static PropertyFileReader mPropertyFileReader = null;

    public static SessionManager getInstance(Context context) {
        if (mSessionManager != null)
            return mSessionManager;

        mContext = context;
        mPropertyFileReader = mPropertyFileReader.getInstance(context);
        loadProjectSharedPreferences();
        mSessionManager = new SessionManager();

        return mSessionManager;

    }

    public static SessionManager Instance() throws IllegalArgumentException {
        if (mSessionManager != null)
            return mSessionManager;
        else
            throw new IllegalArgumentException("No instance is yet created");
    }

    private static void loadProjectSharedPreferences() {
        if (mProjectSharedPref == null) {
            mProjectSharedPref = mContext.getSharedPreferences(PROJECT_PREFERENCES, Context.MODE_PRIVATE);
        }

        String versionNumber = mProjectSharedPref.getString(PropertyTypeConstants.VERSION_NUMBER, null);
        Float versionNoValue = versionNumber == null ? 0 : Float.valueOf(versionNumber);

        if (mPropertyFileReader.getVersion() > versionNoValue) {
            boolean sharedPrefChange = addOrUdateSharedPreferences();
            if (!sharedPrefChange)
                Log.e("SharedPref", "No shared preferences are changed");
        }
    }

    private static boolean addOrUdateSharedPreferences() {

        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putInt(PropertyTypeConstants.DATABASE_VERSION_NUMBER, mPropertyFileReader.getDbVersion());
        editor.putString(PropertyTypeConstants.PARTY_URL, mPropertyFileReader.getPartyUrl());
        editor.putString(PropertyTypeConstants.LIKE_PARTY_URL, mPropertyFileReader.getLikePartyUrl());
        editor.putString(PropertyTypeConstants.REMOVE_FAV_PARTY_URL, mPropertyFileReader.getRemoveFav());
        editor.putString(PropertyTypeConstants.ADD_FAV_PARTY_URL, mPropertyFileReader.getAddFav());
        editor.apply();
        return true;
    }

    private SessionManager() {
    }

    public String getDatabaseDeviceFullPath() {
        return mProjectSharedPref.getString(PropertyTypeConstants.DATABASE_DEVICE_FULLPATH, null);
    }

    public int getDatabaseVersion() {
        Log.d("DB version", "##" + mProjectSharedPref.getInt(PropertyTypeConstants.DATABASE_VERSION_NUMBER, 0));
        return mProjectSharedPref.getInt(PropertyTypeConstants.DATABASE_VERSION_NUMBER, 0);
    }

    public long getUserId() {
        return mProjectSharedPref.getLong(PropertyTypeConstants.USER_ID, 0);
    }

    public String getEmail() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_EMAIL, null);
    }

    public String getPartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.PARTY_URL, null);
    }

    public String getLikePartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.LIKE_PARTY_URL, null);
    }

    public String removeFavPartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.REMOVE_FAV_PARTY_URL, null);
    }

    public String addFavPartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.ADD_FAV_PARTY_URL, null);
    }
}
