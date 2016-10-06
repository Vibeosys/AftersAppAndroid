package com.aftersapp.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by akshay on 19-09-2016.
 */
public class PropertyFileReader {

    private static PropertyFileReader mPropertyFileReader = null;
    private static Context mContext;
    protected static Properties mProperties;

    public static PropertyFileReader getInstance(Context context) {
        if (mPropertyFileReader != null)
            return mPropertyFileReader;

        mContext = context;
        mProperties = getProperties();
        mPropertyFileReader = new PropertyFileReader();
        return mPropertyFileReader;
    }

    protected static Properties getProperties() {
        try {
            AssetManager assetManager = mContext.getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            mProperties = new Properties();
            mProperties.load(inputStream);

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        return mProperties;
    }

    protected String getEndPointUri() {
        return mProperties.getProperty(PropertyTypeConstants.API_ENDPOINT_URI);
    }

    public String getDatabaseDeviceFullPath() {
        return mProperties.getProperty(PropertyTypeConstants.DATABASE_DEVICE_FULLPATH);
    }

    public float getVersion() {
        String versionNumber = mProperties.getProperty(PropertyTypeConstants.VERSION_NUMBER);
        return Float.valueOf(versionNumber);
    }


    public String getDatabaseDirPath() {
        return mProperties.getProperty(PropertyTypeConstants.DATABASE_DIR_PATH);
    }

    public String getDatabaseFileName() {
        return mProperties.getProperty(PropertyTypeConstants.DATABASE_FILE_NAME);
    }

    public int getDbVersion() {
        String versionNumber = mProperties.getProperty(PropertyTypeConstants.DATABASE_VERSION_NUMBER);
        return Integer.valueOf(versionNumber);
    }

    public String getPartyUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.PARTY_URL);
    }

    public String getLikePartyUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.LIKE_PARTY_URL);
    }

    public String getRemoveFav() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.REMOVE_FAV_PARTY_URL);
    }

    public String getAddFav() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.ADD_FAV_PARTY_URL);
    }

    public String getHostPartyUrl() {

        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.POST_PART_URL);
    }

    public String getRegisterUserUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.REGISTER_USER);
    }

    public String getEditProfileUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.EDIT_PROFILE_URL);
    }

    public String getDeletePartyUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.DELETE_PARTY_URL);
    }

    public String getEditPartyUrl() {
        return getEndPointUri() +mProperties.getProperty(PropertyTypeConstants.EDIT_PARTY_URL);
    }
}

