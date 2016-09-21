package com.aftersapp.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.aftersapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditMyProfileFragment extends BaseFragment implements View.OnClickListener {
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
    private int mSwitchValNotification;



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
        mSaveUserProfile.setOnClickListener(this);
        mCancelUserProfile.setOnClickListener(this);
        //mSpinnerSelectionVal get from shared preferences
        //mSwitchValNotification get from shared preferences
        mSwitchValNotification =0;
        mSpinnerSelectionVal="Female";
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
                   Toast toast = Toast.makeText(getContext(),"User Notification is clicked",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
               }
               else
               {
                   Toast toast = Toast.makeText(getContext(),"User Notification is not clicked",Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
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
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDateOfBirth.setText(sdf.format(myCalendar.getTime()));
        mDateOfBirth.setError(null);
    }


    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch(id)
        {
            case R.id.saveProfile:
                callToValidation();
                break;
            case R.id.cancelProfile:
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT_CANCEL_PROFILE).commit();
                break;

        }
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
}
