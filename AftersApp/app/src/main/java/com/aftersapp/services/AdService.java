package com.aftersapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.aftersapp.AftersAppApplication;
import com.aftersapp.MainActivity;
import com.aftersapp.utils.NetworkUtils;
import com.google.android.gms.ads.AdRequest;

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
                    wait(180000);
                    if (NetworkUtils.isActiveNetworkAvailable(getApplicationContext())) {
                        MainActivity.loadAd(new Runnable() {
                            @Override
                            public void run() {
                                AdRequest adRequest = new AdRequest.Builder().addTestDevice("1C22DEC8AEF4249E83143364E2E5AC32").build();
                                MainActivity.mInterstitialAd.loadAd(adRequest);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e(TAG, "##Error occurred in ad service " + e.toString());
                }
            }
        }
    }
}
