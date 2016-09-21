package com.aftersapp.data.responsedata;

import android.util.Log;

import com.aftersapp.data.BaseDTO;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Created by akshay on 21-09-2016.
 */
public class RegisterResponseData extends BaseDTO {

    private static final String TAG = RegisterResponseData.class.getSimpleName();
    private long userId;


    public RegisterResponseData() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public static RegisterResponseData deserializeJson(String serializedString) {
        Gson gson = new Gson();
        RegisterResponseData responseDTO = null;
        try {
            responseDTO = gson.fromJson(serializedString, RegisterResponseData.class);
        } catch (JsonParseException e) {
            Log.d(TAG, "Exception in deserialization RegisterResponseData" + e.toString());
        }
        return responseDTO;
    }
}
