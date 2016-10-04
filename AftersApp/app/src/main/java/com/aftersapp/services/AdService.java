package com.aftersapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.aftersapp.AftersAppApplication;
import com.aftersapp.MainActivity;
import com.aftersapp.utils.NetworkUtils;

/**
 * Created by akshay on 03-10-2016.
 */
public class AdService extends IntentService {

    private static final String TAG = AdService.class.getSimpleName();

    public AdService() {
        super(AdService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            synchronized (this) {
                try {
                    wait(18000);
                    //if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext()))
                    //MainActivity.showAds();

                } catch (Exception e) {
                    Log.e(TAG, "##Error occurred in ad service " + e.toString());
                }
            }
        }
    }
}
