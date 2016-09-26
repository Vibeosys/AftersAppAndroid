package com.aftersapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aftersapp.helper.ChatHelper;
import com.aftersapp.interfaces.chatinterfaces.QbSessionStateCallback;
import com.aftersapp.utils.qbutils.SharedPreferencesUtil;
import com.quickblox.auth.QBAuth;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.Date;

/**
 * Created by akshay on 26-09-2016.
 */
public abstract class ChatBaseFragment extends BaseFragment implements QbSessionStateCallback {

    private static final String TAG = ChatBaseFragment.class.getSimpleName();

    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    protected ActionBar actionBar;
    protected boolean isAppSessionActive;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean wasAppRestored = savedInstanceState != null;
        boolean isQbSessionActive = isSessionActive();
        final boolean needToRestoreSession = wasAppRestored || !isQbSessionActive;
        Log.v(TAG, "wasAppRestored = " + wasAppRestored);
        Log.v(TAG, "isQbSessionActive = " + isQbSessionActive);

        // Triggering callback via Handler#post() method
        // to let child's code in onCreate() to execute first
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (needToRestoreSession) {
                    recreateChatSession();
                    isAppSessionActive = false;
                } else {
                    onSessionCreated(true);
                    isAppSessionActive = true;
                }
            }
        });
    }

    public static boolean isSessionActive() {
        try {
            String token = QBAuth.getBaseService().getToken();
            Date expirationDate = QBAuth.getBaseService().getTokenExpirationDate();

            if (TextUtils.isEmpty(token)) {
                return false;
            }

            if (System.currentTimeMillis() >= expirationDate.getTime()) {
                return false;
            }

            return true;
        } catch (BaseServiceException ignored) {
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("dummy_value", 0);
        super.onSaveInstanceState(outState);
    }

    private void recreateChatSession() {
        Log.d(TAG, "Need to recreate chat session");

        QBUser user = SharedPreferencesUtil.getQbUser();
        if (user == null) {
            throw new RuntimeException("User is null, can't restore session");
        }

        reloginToChat(user);
    }

    private void reloginToChat(final QBUser user) {
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                Log.v(TAG, "Chat login onSuccess()");
                isAppSessionActive = true;
                onSessionCreated(true);
            }

            @Override
            public void onError(QBResponseException e) {
                isAppSessionActive = false;
                Log.w(TAG, "Chat login onError(): " + e);
                onSessionCreated(false);
            }
        });
    }
}
