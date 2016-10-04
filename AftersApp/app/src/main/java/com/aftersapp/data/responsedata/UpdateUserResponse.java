package com.aftersapp.data.responsedata;

import android.util.Log;

import com.aftersapp.data.BaseDTO;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Created by akshay on 04-10-2016.
 */
public class UpdateUserResponse extends BaseDTO {

    private static final String TAG = UpdateUserResponse.class.getSimpleName();
    private String dob;
    private String email;
    private String email2;
    private String token;
    private String gender;
    private String name;
    private String phone;
    private String profileImage;
    private long userId;
    private int emailNotify;

    public UpdateUserResponse() {
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getEmailNotify() {
        return emailNotify;
    }

    public void setEmailNotify(int emailNotify) {
        this.emailNotify = emailNotify;
    }

    public static UpdateUserResponse deserializeJson(String serializedString) {
        Gson gson = new Gson();
        UpdateUserResponse responseDTO = null;
        try {
            responseDTO = gson.fromJson(serializedString, UpdateUserResponse.class);
        } catch (JsonParseException e) {
            Log.d(TAG, "Exception in deserialization RegisterResponseData" + e.toString());
        }
        return responseDTO;
    }
}
