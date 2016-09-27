package com.aftersapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aftersapp.MainActivity;
import com.aftersapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfileFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mUserName, mUserEmailId, mUserDateOfBirth, mUserGender,mUserNotificationStatus
            ,mUserNameFirst;
    private CircleImageView circleView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private AdView mAdView;
    private Button mRemoveAds;
    AdRequest adRequest;


    public ViewProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProfileFragment newInstance(String param1, String param2) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mAdView = (AdView) view.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().addTestDevice("DC7854A3ADFE5403F956AFB5B83C7391")
                .build();
        mAdView.loadAd(adRequest);

        mRemoveAds = (Button) view.findViewById(R.id.removeAD);


        mUserName = (TextView) view.findViewById(R.id.userFullNameView);
        mUserEmailId = (TextView) view.findViewById(R.id.userEmailIdView);
        mUserDateOfBirth = (TextView) view.findViewById(R.id.userDOBView);
        mUserGender = (TextView) view.findViewById(R.id.userGendarView);
        circleView = (CircleImageView) view.findViewById(R.id.circleView);
        mUserNotificationStatus = (TextView) view.findViewById(R.id.notificationStatusView);
        mUserNameFirst =(TextView) view.findViewById(R.id.userName);
        DownloadImage downloadImage = new DownloadImage();
        downloadImage.execute(mSessionManager.getProfImg());
        CallToViewProfile();
        mRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseFragment purchaseFragment= new PurchaseFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay,purchaseFragment,"Remove Ads").commit();
            }
        });
        return view;
    }

    private void CallToViewProfile() {
        mUserName.setText(""+mSessionManager.getName());
        mUserEmailId.setText(""+mSessionManager.getEmail2());

        if(TextUtils.isEmpty(mSessionManager.getGender()))
        {
            mUserGender.setText("");
        }else
        {
            mUserGender.setText(""+mSessionManager.getGender());
        }
        if(TextUtils.isEmpty(mSessionManager.getDob()))
        {
            mUserDateOfBirth.setText("");
        }else
        {
            mUserDateOfBirth.setText(""+mSessionManager.getDob());
        }
        long NotificationFlg = mSessionManager.getEmailNotify();
        mUserNameFirst.setText(""+mSessionManager.getName());
        if(NotificationFlg==1)
        {
            mUserNotificationStatus.setText("Enable");
        }
        else {
            mUserNotificationStatus.setText("Disable");
        }
    }


    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            circleView.setImageBitmap(result);
        }
    }

}
