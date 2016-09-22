package com.aftersapp.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.UpdateProfileDTO;
import com.aftersapp.utils.DateUtils;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMyProfileFragment extends BaseFragment implements View.OnClickListener,ServerSyncManager.OnSuccessResultReceived, ServerSyncManager.OnErrorResultReceived  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EditText mDateOfBirth,mUserName,mUserEmailId;
    Calendar myCalendar;
    private Spinner mSpinner;
    private Button mSaveUserProfile,mCancelUserProfile;
    private Switch mUserNotoficationSwitch;
    private static final String HOME_FRAGMENT_CANCEL_PROFILE = "home";
    private String mSpinnerSelectionVal;
    private long mSwitchValNotification;
    private TextView mUserNameFirst;
    private CircleImageView mEditUserImage;
    private static final String HOME_FRAGMENT_EDIT_PROFILE="home";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_my_profile, container, false);
        mDateOfBirth =(EditText) rootView.findViewById(R.id.userDOB);
        mSpinner =(Spinner) rootView.findViewById(R.id.spinner);
        mUserName = (EditText) rootView.findViewById(R.id.userFullName);
        mUserEmailId = (EditText) rootView.findViewById(R.id.userEmailId);
        mSaveUserProfile = (Button) rootView.findViewById(R.id.saveProfile);
        mCancelUserProfile = (Button) rootView.findViewById(R.id.cancelProfile);
        mUserNotoficationSwitch =(Switch) rootView.findViewById(R.id.switch1);
        mUserNameFirst = (TextView) rootView.findViewById(R.id.userName);
        mEditUserImage =(CircleImageView) rootView.findViewById(R.id.circleView);
        mSaveUserProfile.setOnClickListener(this);
        mCancelUserProfile.setOnClickListener(this);
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);

        mUserName.setText(""+mSessionManager.getName());
        mUserNameFirst.setText(""+mSessionManager.getName());
        mUserEmailId.setText(""+mSessionManager.getEmail2());
        mDateOfBirth.setText(""+mSessionManager.getDob());
        DownloadImage downloadImage = new DownloadImage();
        downloadImage.execute(mSessionManager.getProfImg());
        //mSpinnerSelectionVal get from shared preferences
        //mSwitchValNotification get from shared preferences
       // mSwitchValNotification =0;
      //  mSpinnerSelectionVal="Female";
        mSwitchValNotification =mSessionManager.getEmailNotify();
        mSpinnerSelectionVal=mSessionManager.getGender();
        List<String> spineerData =  new ArrayList<>();
        spineerData.add("Male");
        spineerData.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,spineerData);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(dataAdapter);

        if(!mSpinnerSelectionVal.equals(null))
        {
           int spineerPosition = dataAdapter.getPosition(mSpinnerSelectionVal);
            mSpinner.setSelection(spineerPosition);
        }
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerSelectionVal = mSpinner.getItemAtPosition(position).toString();
                Log.d("TAG","TAG");
                Log.d("TAG","TAG");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(mSwitchValNotification==1)
        {
            mUserNotoficationSwitch.setChecked(true);
        }else if(mSwitchValNotification==0)
        {
            mUserNotoficationSwitch.setChecked(false);
        }
        mDateOfBirth.setInputType(InputType.TYPE_NULL);

        mDateOfBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myCalendar = Calendar.getInstance();
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });

        mUserNotoficationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(mUserNotoficationSwitch.isChecked())
               {

                   mSwitchValNotification=1;
               }
               else
               {

                   mSwitchValNotification=0;
               }
           }
       });
        return rootView;
    }



    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    private void updateLabel() {
      //  String myFormat = "dd-MMM-yyyy"; //In which you need put here
        //SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        DateUtils dateUtils = new DateUtils();
        mDateOfBirth.setText(dateUtils.getLocalDateInFormat(myCalendar.getTime()));
       // mDateOfBirth.setText(sdf.format(myCalendar.getTime()));
        mDateOfBirth.setError(null);
    }


    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch(id)
        {
            case R.id.saveProfile:
                boolean returnVal =callToValidation();
                if(returnVal==true)
                {
                    CallToWebServices();
                }
                break;
            case R.id.cancelProfile:
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT_CANCEL_PROFILE).commit();
                break;

        }
    }

    private void CallToWebServices() {

        String userEmail = mUserEmailId.getText().toString();
        String userFullName = mUserName.getText().toString();
        String userDate = mDateOfBirth.getText().toString();
        int NotifyVal =(int) mSwitchValNotification;
        UpdateProfileDTO updateProfileDTO = new UpdateProfileDTO(mSessionManager.getUserId(),userFullName,mSessionManager.getEmail(),
                userEmail,"123456789",mSpinnerSelectionVal,mSessionManager.getProfImg(),userDate,mSessionManager.getToken(),NotifyVal);

        String url = mSessionManager.getEditProfileUrl();
        Gson gson = new Gson();
        String serlize =gson.toJson(updateProfileDTO);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serlize);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_EDIT_PROFILE,
                mSessionManager.getEditProfileUrl(), baseRequestDTO);
        Log.d("TAG","TAG");
        Log.d("TAG","TAG");

    }

    private boolean callToValidation() {
            if(TextUtils.isEmpty(mUserName.getText().toString().trim()))
            {
                mUserName.requestFocus();
                mUserName.setError("Please enter userName");
                return false;
            }else if(TextUtils.isEmpty(mUserEmailId.getText().toString().trim()))
            {
                mUserEmailId.requestFocus();
                mUserEmailId.setError("Please enter email id");
                return false;
            }
        return true;
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_EDIT_PROFILE:
                Log.e("TAG", "##Volley Server error " + error.toString());
                break;
        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_EDIT_PROFILE:
                Log.d("TAG", "##Volley Data error " + errorMessage);
                break;
        }

    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_EDIT_PROFILE:
                Toast toast = Toast.makeText(getContext(),"Profile Updated Successfully",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT_EDIT_PROFILE).commit();
                break;
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
            mEditUserImage.setImageBitmap(result);
        }
    }
}
