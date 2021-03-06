package com.aftersapp.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.aftersapp.AftersAppApplication;
import com.aftersapp.R;
import com.aftersapp.utils.SessionManager;
import com.aftersapp.utils.SharedPrefsHelper;
import com.aftersapp.utils.VersionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by akshay on 23-09-2016.
 */
public class GooglePlayServicesHelper {
    private static final String TAG = GooglePlayServicesHelper.class.getSimpleName();

    private static final String PREF_APP_VERSION = "appVersion";
    private static final String PREF_GCM_REG_ID = "registration_id";

    private static final int PLAY_SERVICES_REQUEST_CODE = 9000;
    protected static SessionManager mSessionManager = null;

    public GooglePlayServicesHelper() {
    }

    public GooglePlayServicesHelper(SessionManager mSessionManager) {
        this.mSessionManager = mSessionManager;
    }

    public void registerForGcm(String senderId) {
        String gcmRegId = getGcmRegIdFromPreferences();
        if (TextUtils.isEmpty(gcmRegId)) {
            registerInGcmInBackground(senderId);
        }
    }

    public void unregisterFromGcm(String senderId) {
        String gcmRegId = getGcmRegIdFromPreferences();
        if (!TextUtils.isEmpty(gcmRegId)) {
            unregisterInGcmInBackground(senderId);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @param activity activity where you check Google Play Services availability
     */
    public boolean checkPlayServicesAvailable(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_REQUEST_CODE)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public boolean checkPlayServicesAvailable() {
        return getPlayServicesAvailabilityResultCode() == ConnectionResult.SUCCESS;
    }

    private int getPlayServicesAvailabilityResultCode() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(AftersAppApplication.getInstance());
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInGcmInBackground(String senderId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(AftersAppApplication.getInstance());
                    return instanceID.getToken(AftersAppApplication.getInstance().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    Log.w(TAG, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final String gcmRegId) {
                if (TextUtils.isEmpty(gcmRegId)) {
                    Log.w(TAG, "Device wasn't registered in GCM");
                } else {
                    Log.i(TAG, "Device registered in GCM, regId=" + gcmRegId);
                    String email = mSessionManager.getEmail();
                    String password = mSessionManager.getEmail() + mSessionManager.getUserId();
                    final QBUser user = new QBUser(email, password);
                    QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            QBSubscription qbSubscription = new QBSubscription();
                            qbSubscription.setNotificationChannel(QBNotificationChannel.GCM);
                            qbSubscription.setDeviceUdid(getDeviceUid());
                            qbSubscription.setRegistrationID(gcmRegId);
                            qbSubscription.setEnvironment(QBEnvironment.PRODUCTION); // Don't forget to change QBEnvironment to PRODUCTION when releasing application

                            QBPushNotifications.createSubscription(qbSubscription,
                                    new QBEntityCallback<ArrayList<QBSubscription>>() {
                                        @Override
                                        public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {
                                            Log.i(TAG, "Successfully subscribed for QB push messages");
                                            saveGcmRegIdToPreferences(gcmRegId);
                                        }

                                        @Override
                                        public void onError(QBResponseException error) {
                                            Log.w(TAG, "Unable to subscribe for QB push messages; " + error.toString());
                                        }
                                    });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                }
            }
        }.execute(senderId);
    }

    private void unregisterInGcmInBackground(String senderId) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(AftersAppApplication.getInstance());
                    instanceID.deleteToken(params[0], GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    return null;
                } catch (IOException e) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    Log.w(TAG, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void gcmRegId) {
                deleteGcmRegIdFromPreferences();
            }
        }.execute(senderId);
    }

    protected void saveGcmRegIdToPreferences(String gcmRegId) {
        int appVersion = VersionUtils.getAppVersion();
        Log.i(TAG, "Saving gcmRegId on app version " + appVersion);

        // We save both gcmRegId and current app version,
        // so we can check if app was updated next time we need to get gcmRegId
        SharedPrefsHelper.getInstance().save(PREF_GCM_REG_ID, gcmRegId);
        SharedPrefsHelper.getInstance().save(PREF_APP_VERSION, appVersion);
    }

    protected void deleteGcmRegIdFromPreferences() {
        SharedPrefsHelper.getInstance().delete(PREF_GCM_REG_ID);
        SharedPrefsHelper.getInstance().delete(PREF_APP_VERSION);
    }

    protected String getGcmRegIdFromPreferences() {
        // Check if app was updated; if so, we must request new gcmRegId
        // since the existing gcmRegId is not guaranteed to work
        // with the new app version
        int registeredVersion = SharedPrefsHelper.getInstance().get(PREF_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = VersionUtils.getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }

        return SharedPrefsHelper.getInstance().get(PREF_GCM_REG_ID, "");
    }

    public static String getDeviceUid() {
        Context context = AftersAppApplication.getInstance();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String uniqueDeviceId = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(uniqueDeviceId)) {
            // for tablets
            ContentResolver cr = context.getContentResolver();
            uniqueDeviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        }

        return uniqueDeviceId;
    }
}