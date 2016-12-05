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
        editor.putString(PropertyTypeConstants.POST_PART_URL, mPropertyFileReader.getHostPartyUrl());
        editor.putString(PropertyTypeConstants.REGISTER_USER, mPropertyFileReader.getRegisterUserUrl());
        editor.putString(PropertyTypeConstants.EDIT_PROFILE_URL, mPropertyFileReader.getEditProfileUrl());
        editor.putString(PropertyTypeConstants.DELETE_PARTY_URL, mPropertyFileReader.getDeletePartyUrl());
        editor.putString(PropertyTypeConstants.EDIT_PARTY_URL,mPropertyFileReader.getEditPartyUrl());
        editor.putString(PropertyTypeConstants.SIGN_UP_USER,mPropertyFileReader.getUserSignUp());
        editor.putString(PropertyTypeConstants.SIGN_IN_USER,mPropertyFileReader.getUserSignIn());
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

    public String getName() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_NAME, null);
    }

    public String getEmail2() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_EMAIL2, null);
    }

    public String getPhone() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_PHONE, null);
    }

    public String getGender() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_GENDER, null);
    }

    public String getProfImg() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_PROF_IMG, null);
    }

    public String getDob() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_DOB, null);
    }

    public String getToken() {
        return mProjectSharedPref.getString(PropertyTypeConstants.USER_TOKEN, null);
    }

    public long getEmailNotify() {
        return mProjectSharedPref.getLong(PropertyTypeConstants.USER_EMAIL_NOTIFY, 0);
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

    public String getHostPartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.POST_PART_URL, null);
    }
    public String getSignUpUrl()
    {
        return mProjectSharedPref.getString(PropertyTypeConstants.SIGN_UP_USER,null);
    }
    public void setUserId(long userId) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_ID, userId);
    }

    public String getEditProfileUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.EDIT_PROFILE_URL, null);
    }
    public String getEditPartyUrl()
    {
        return mProjectSharedPref.getString(PropertyTypeConstants.EDIT_PARTY_URL, null);
    }

    private static void setValuesInSharedPrefs(String sharedPrefKey, long sharedPrefValue) {
        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putLong(sharedPrefKey, sharedPrefValue);
        editor.apply();
    }

    private static void setIntValuesInSharedPrefs(String sharedPrefKey, int sharedPrefValue) {
        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putInt(sharedPrefKey, sharedPrefValue);
        editor.apply();
    }

    private static void setValuesInSharedPrefs(String sharedPrefKey, String sharedPrefValue) {
        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putString(sharedPrefKey, sharedPrefValue);
        editor.apply();
    }

    public void setName(String name) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_NAME, name);
    }

    public void setEmail(String email) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_EMAIL, email);
    }

    public void setEmail2(String email) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_EMAIL2, email);
    }

    public void setPhone(String phone) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_PHONE, phone);
    }

    public void setGender(String gender) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_GENDER, gender);
    }

    public void setProfileImg(String profileImg) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_PROF_IMG, profileImg);
    }

    public void setDob(String dob) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_DOB, dob);
    }

    public void setToken(String token) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_TOKEN, token);
    }
    public void setEmailNotify(int emailNotify) {
        setValuesInSharedPrefs(PropertyTypeConstants.USER_EMAIL_NOTIFY, emailNotify);
    }

    public String registerUser() {
        return mProjectSharedPref.getString(PropertyTypeConstants.REGISTER_USER, null);
    }

    public void setIsPurchased(int isPurchased) {
        setIntValuesInSharedPrefs(PropertyTypeConstants.IS_PURCHASED, isPurchased);
    }

    public int getIsPurchased() {
        return mProjectSharedPref.getInt(PropertyTypeConstants.IS_PURCHASED, 0);
    }

    public void setRadius(int radius) {
        setIntValuesInSharedPrefs(PropertyTypeConstants.FILTER_RADIUS, radius);
    }

    public int getRadius() {
        return mProjectSharedPref.getInt(PropertyTypeConstants.FILTER_RADIUS, AppConstants.DEFAULT_RADIUS_VALUE);
    }

    public void setAge(int age) {
        setIntValuesInSharedPrefs(PropertyTypeConstants.FILTER_AGE, age);
    }

    public int getAge() {
        return mProjectSharedPref.getInt(PropertyTypeConstants.FILTER_AGE, AppConstants.DEFAULT_AGE_VALUE);
    }

    public void setMusicGenre(String musicGenre) {
        setValuesInSharedPrefs(PropertyTypeConstants.FILTER_MUSIC, musicGenre);
    }

    public String getMusicGenre() {
        return mProjectSharedPref.getString(PropertyTypeConstants.FILTER_MUSIC, null);
    }

    public int getMessageCount() {
        return mProjectSharedPref.getInt(PropertyTypeConstants.MESSAGE_COUNT, 0);
    }

    public void setMessageCount(int messageCount) {
        setIntValuesInSharedPrefs(PropertyTypeConstants.MESSAGE_COUNT, messageCount);
    }

    public int getDisclaimerVal() {
        return mProjectSharedPref.getInt(PropertyTypeConstants.DISCLAIMER_VAL, 0);
    }

    public void setDisclaimerVal(int userVal) {
        setIntValuesInSharedPrefs(PropertyTypeConstants.DISCLAIMER_VAL, userVal);
    }

    public String getDeletePartyUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.DELETE_PARTY_URL, null);
    }

    public String getFilteredDate() {
        return mProjectSharedPref.getString(PropertyTypeConstants.FILTER_DATE, null);
    }

    public void setFilterDate(String setFilterDate) {
        setValuesInSharedPrefs(PropertyTypeConstants.FILTER_DATE, setFilterDate);
    }

    public String getSignInUrl() {
        return mProjectSharedPref.getString(PropertyTypeConstants.SIGN_IN_USER,null);
    }
}
