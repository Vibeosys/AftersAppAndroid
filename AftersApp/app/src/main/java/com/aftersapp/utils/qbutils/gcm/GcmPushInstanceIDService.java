package com.aftersapp.utils.qbutils.gcm;

import com.aftersapp.utils.QuickBlocsConst;

public class GcmPushInstanceIDService extends CoreGcmPushInstanceIDService {
    @Override
    protected String getSenderId() {
        return QuickBlocsConst.GCM_SENDER_ID;
    }
}
