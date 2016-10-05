package com.aftersapp.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aftersapp.MainActivity;
import com.aftersapp.R;
import com.aftersapp.utils.AppConstants;
import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT = 3000;
    IInAppBillingService mService;
    ServiceConnection mServiceConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                isAppPurchased();
            }
        };
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent mainRun = new Intent(SplashScreenActivity.this, DisclaimerActivity.class);
                startActivity(mainRun);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void isAppPurchased() {
        int isBillingSupported = -1;
        try {
            isBillingSupported = mService.isBillingSupported(3, getPackageName(), "inapp");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (isBillingSupported != 0) {

        }
        //Toast.makeText(getApplicationContext(), "The Billing is not supported", Toast.LENGTH_SHORT).show();
        else
            getPurchases();
    }

    public void getPurchases() {
        try {
            Bundle purchaseItems = mService.getPurchases(3, getPackageName(), "inapp", null);
            int responseCode = purchaseItems.getInt("RESPONSE_CODE");
            if (responseCode == 0) {
                ArrayList<String> items = purchaseItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                if (items.contains("com.aftersapp.noads")) {
                    mSessionManager.setIsPurchased(AppConstants.ITEM_PURCHASED);
                } else {
                    mSessionManager.setIsPurchased(AppConstants.ITEM_PURCHASED);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
