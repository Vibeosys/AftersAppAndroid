package com.aftersapp.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aftersapp.activities.LoginActivity;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.UserRequestDTO;
import com.aftersapp.data.responsedata.BaseResponseDTO;
import com.aftersapp.database.DbRepository;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by akshay on 19-09-2016.
 */
public class ServerSyncManager {

    private SessionManager mSessionManager;
    private Context mContext;
    private DbRepository mDbRepository = null;
    private OnSuccessResultReceived mOnSuccessResultReceived;
    private OnErrorResultReceived mErrorReceived;
    private String TAG = ServerSyncManager.class.getSimpleName();


    public ServerSyncManager() {

    }

    public ServerSyncManager(@NonNull Context context, @NonNull SessionManager sessionManager) {
        mContext = context;
        mSessionManager = sessionManager;
        mDbRepository = new DbRepository(mContext, mSessionManager);
    }

    public void uploadDataToServer(int requestToken, String url, BaseRequestDTO params) {
        /*if (mSessionManager.getUserId() == 0 || mSessionManager.getUserName() == null
                || mSessionManager.getUserName().isEmpty()) {
            Log.e("UserNotAuth", "User is not authenticated before upload");
            return;
        }*/
        String uploadJson = prepareUploadJsonFromData(params);
        Log.i(TAG, "## Data request" + url);
        uploadJsonToServer(uploadJson, url, requestToken);
    }

    public void setOnStringResultReceived(OnSuccessResultReceived stringResultReceived) {
        mOnSuccessResultReceived = stringResultReceived;
    }

    public void setOnStringErrorReceived(OnErrorResultReceived stringErrorReceived) {
        mErrorReceived = stringErrorReceived;
    }

    private String prepareUploadJsonFromData(BaseRequestDTO params) {

        UserRequestDTO userRequestDTO = new UserRequestDTO(mSessionManager.getUserId(),
                mSessionManager.getEmail());
       /* UserRequestDTO userRequestDTO = new UserRequestDTO(mSessionManager.getUserId(),
                mSessionManager.getEmail());*/
        //get the values from session manager
        params.setUser(userRequestDTO);
        String uploadJson = params.serializeString();
        Log.i(TAG, "## request json" + uploadJson);
        return uploadJson;
    }

    private void uploadJsonToServer(String uploadJson, String uploadUrl,
                                    final int requestToken) {
        RequestQueue vollyRequest = Volley.newRequestQueue(mContext);
        JsonObjectRequest uploadRequest = new JsonObjectRequest(Request.Method.POST,
                uploadUrl, uploadJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "## Response data" + response.toString());
                BaseResponseDTO responseDTO = BaseResponseDTO.deserializeJson(response.toString());
                if (responseDTO == null) {
                    Log.e(TAG, "Error to get the data from server");
                    return;
                }
                if (responseDTO.getErrorCode() == 110) {
                    callToLogOut();
                    return;
                }
                if (responseDTO.getErrorCode() > 0) {
                    if (mErrorReceived != null)
                        mErrorReceived.onDataErrorReceived(responseDTO.getErrorCode(), responseDTO.getMessage()
                                , requestToken);
                    Log.e("Data Error", "Error to get the data");
                    return;
                }

                if (mOnSuccessResultReceived != null) {
                    mOnSuccessResultReceived.onResultReceived(responseDTO.getData(), requestToken);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (mErrorReceived != null)
                    mErrorReceived.onVolleyErrorReceived(error, requestToken);
            }
        });
        uploadRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        vollyRequest.add(uploadRequest);
    }

    private void callToLogOut() {

        mDbRepository.deleteAllData();
        //UserAuth.CleanAuthenticationInfo();
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(loginIntent);
        //finish();
    }


    public interface OnSuccessResultReceived {
        void onResultReceived(@NonNull String data, int requestToken);
    }


    public interface OnErrorResultReceived {
        void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken);

        void onDataErrorReceived(int errorCode, String errorMessage, int requestToken);
    }
}
