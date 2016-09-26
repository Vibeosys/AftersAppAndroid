package com.aftersapp.utils.qbutils.gcm;

import android.os.Bundle;
import android.util.Log;

import com.aftersapp.interfaces.GcmConsts;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by akshay on 26-09-2016.
 */
public abstract class CoreGcmPushListenerService extends GcmListenerService {
    private static final String TAG = CoreGcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString(GcmConsts.EXTRA_GCM_MESSAGE);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);

        if (ActivityLifecycle.getInstance().isBackground()) {
            showNotification(message);
        }

        sendPushMessageBroadcast(message);
    }

    protected abstract void showNotification(String message);

    protected abstract void sendPushMessageBroadcast(String message);
}
