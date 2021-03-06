package com.aftersapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aftersapp.database.DbRepository;
import com.aftersapp.services.GPSTracker;
import com.aftersapp.utils.DialogUtils;
import com.aftersapp.utils.ServerSyncManager;
import com.aftersapp.utils.SessionManager;

/**
 * Created by akshay on 16-09-2016.
 */
public class BaseFragment extends Fragment {

    protected ServerSyncManager mServerSyncManager = null;
    protected DbRepository mDbRepository = null;
    protected static SessionManager mSessionManager = null;
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionManager = SessionManager.getInstance(getActivity().getApplicationContext());
        Log.d("##", "##" + mSessionManager.getDatabaseDeviceFullPath());
        mServerSyncManager = new ServerSyncManager(getActivity().getApplicationContext(), mSessionManager);
        mDbRepository = new DbRepository(getActivity().getApplicationContext(), mSessionManager);
        progressDialog = DialogUtils.getProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show, final View hideFormView, final View showProgressView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                if (hideFormView != null) {
                    hideFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    hideFormView.animate().setDuration(shortAnimTime).alpha(
                            show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            hideFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
                }
                if (showProgressView != null) {
                    showProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    showProgressView.animate().setDuration(shortAnimTime).alpha(
                            show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                showProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                hideFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (IllegalStateException e) {

        }

    }

    protected void createAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // whatever...
                    }
                }).create().show();
    }
}
