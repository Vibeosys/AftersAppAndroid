package com.aftersapp.utils.qbutils.gcm;

import com.aftersapp.helper.GooglePlayServicesHelper;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by akshay on 26-09-2016.
 */
public abstract class CoreGcmPushInstanceIDService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        GooglePlayServicesHelper playServicesHelper = new GooglePlayServicesHelper();
        if (playServicesHelper.checkPlayServicesAvailable()) {
            playServicesHelper.registerForGcm(getSenderId());
        }
    }

    protected abstract String getSenderId();
}