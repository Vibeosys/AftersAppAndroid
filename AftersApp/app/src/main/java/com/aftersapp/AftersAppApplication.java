package com.aftersapp;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.aftersapp.utils.QuickBlocsConst;
import com.aftersapp.utils.qbutils.gcm.ActivityLifecycle;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.quickblox.core.QBSettings;

/**
 * Created by akshay on 20-09-2016.
 */
public class AftersAppApplication extends MultiDexApplication {

    private static AftersAppApplication instance;
    private static int clickCount = 0;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        instance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ActivityLifecycle.init(this);
        QBSettings.getInstance().init(getApplicationContext(), QuickBlocsConst.APP_ID,
                QuickBlocsConst.AUTH_KEY, QuickBlocsConst.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(QuickBlocsConst.ACCOUNT_KEY);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstial_ad_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // super.onAdLoaded();
                showIntestititalCase();
            }
        });
    }

    public static synchronized AftersAppApplication getInstance() {
        return instance;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCountZero() {
        clickCount = 0;
    }

    public void setAddClickCount() {
        clickCount = clickCount + 1;
        if (clickCount == 6) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("1C22DEC8AEF4249E83143364E2E5AC32").build();
            mInterstitialAd.loadAd(adRequest);
            clickCount = 0;
        }

    }

    public void showIntestititalCase() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
