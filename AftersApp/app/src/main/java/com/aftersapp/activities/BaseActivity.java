package com.aftersapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aftersapp.database.DbRepository;
import com.aftersapp.utils.DialogUtils;
import com.aftersapp.utils.ServerSyncManager;
import com.aftersapp.utils.SessionManager;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by akshay on 16-09-2016.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected ServerSyncManager mServerSyncManager = null;
    protected DbRepository mDbRepository = null;
    protected static SessionManager mSessionManager = null;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionManager = SessionManager.getInstance(getApplicationContext());
        Log.d("##", "##" + mSessionManager.getDatabaseDeviceFullPath());
        mServerSyncManager = new ServerSyncManager(getApplicationContext(), mSessionManager);
        mDbRepository = new DbRepository(getApplicationContext(), mSessionManager);
        mDbRepository.getDatabaseStructure();
        progressDialog = DialogUtils.getProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Date currentDate = new Date();
        try {
            QBAuth.createFromExistentToken("31ed199120fb998dc472aea785a1825809ad5c04", currentDate);
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
        String token = null;
        try {
            token = BaseService.getBaseService().getToken();
            Date expirationDate = BaseService.getBaseService().getTokenExpirationDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.HOUR, 2);
            Date twoHoursAfter = cal.getTime();

            if (expirationDate.before(twoHoursAfter)) {
                try {
                    QBAuth.createFromExistentToken(token, expirationDate);
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
            } else {
                createSession();
            }
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }


    }

    private void createSession() {
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}
