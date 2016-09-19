package com.aftersapp.data.responsedata;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Created by akshay on 19-09-2016.
 */
public class BaseResponseDTO {

    private static final String TAG = BaseResponseDTO.class.getSimpleName();
    private int errorCode;
    private String message;
    private String data;


    public BaseResponseDTO() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static BaseResponseDTO deserializeJson(String serializedString) {
        Gson gson = new Gson();
        BaseResponseDTO responseDTO = null;
        try {
            responseDTO = gson.fromJson(serializedString, BaseResponseDTO.class);
        } catch (JsonParseException e) {
            Log.d(TAG, "Exception in deserialization Base" + e.toString());
        }
        return responseDTO;
    }
}
