package com.aftersapp;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.aftersapp.utils.QuickBlocsConst;
import com.aftersapp.utils.SessionManager;
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
    protected static SessionManager mSessionManager = null;

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

        mSessionManager = SessionManager.getInstance(getApplicationContext());
    }

    public static synchronized AftersAppApplication getInstance() {
        return instance;
    }



    public static SessionManager getmSessionManager() {
        return mSessionManager;
    }
}
