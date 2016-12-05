package com.aftersapp.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.aftersapp.R;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.SignUpUserDTO;
import com.aftersapp.utils.DateUtils;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, ServerSyncManager.OnSuccessResultReceived,
        ServerSyncManager.OnErrorResultReceived {
    private EditText mUserName, mUserEmailId, mUserPassword, mUserDOB;
    private Button mSignUpBtn;
    private String TAG;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(getResources().getString(R.string.str_signup_user_title));
        TAG = getClass().getName();
        mUserName = (EditText) findViewById(R.id.userNameEditText);
        mUserEmailId = (EditText) findViewById(R.id.emailIdEditText);
        mUserPassword = (EditText) findViewById(R.id.passwordEditText);
        mUserDOB = (EditText) findViewById(R.id.dateOfBirthEditText);
        mSignUpBtn = (Button) findViewById(R.id.signUp_user);
        mUserDOB.setInputType(InputType.TYPE_NULL);
        mCalendar = Calendar.getInstance();

        mSignUpBtn.setOnClickListener(this);
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);

        mUserDOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCalendar = Calendar.getInstance();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(SignUpActivity.this, date, mCalendar
                            .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        DateUtils dateUtils = new DateUtils();
        String date = dateUtils.getLocalDateInFormat(mCalendar.getTime());
        mUserDOB.setText(date);
        // mStringDate = sdf.format(mCalendar.getTime());
        mUserDOB.setError(null);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.signUp_user:
                boolean result = validationMessage();
                if (result) {
                    progressDialog.show();
                    String UserName = mUserName.getText().toString().trim();
                    String UserEmailId = mUserEmailId.getText().toString().trim();
                    String UserPassword = mUserPassword.getText().toString().trim();
                    String UserDOB = mUserDOB.getText().toString().trim();
                    String formattedDob = null;
                    if (!TextUtils.isEmpty(UserDOB)) {
                        DateUtils dateUtils = new DateUtils();
                        formattedDob = dateUtils.convertFbDateToSwedish(UserDOB);
                    }
                    callToSignUpUser(UserName, UserEmailId, UserPassword, formattedDob);
                }
                break;
            default:
                break;
        }

    }

    private void callToSignUpUser(String UserName, String UserEmailId, String UserPassword, String UserDOB) {
        SignUpUserDTO signUpUserDTO = new SignUpUserDTO(UserName, UserEmailId, UserPassword, UserDOB);
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(signUpUserDTO);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_SIGN_UP_USER,
                mSessionManager.getSignUpUrl(), baseRequestDTO);
        Log.d("TAG", "TAG");
        Log.d("TAG", "TAG");

    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_SIGN_UP_USER:
                Log.e(TAG, "##Volley Server error " + error.toString());
                customAlterDialog(getResources().getString(R.string.str_err_server_err),
                        getResources().getString(R.string.str_err_server_msg));
                progressDialog.dismiss();
                break;

        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_SIGN_UP_USER:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                customAlterDialog(getResources().getString(R.string.str_err_server_err),
                        errorMessage);
                progressDialog.dismiss();
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_SIGN_UP_USER:
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "User Sign up Successfully..", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

    }

    private boolean validationMessage() {
        String UserName = mUserName.getText().toString().trim();
        String UserEmailId = mUserEmailId.getText().toString().trim();
        String UserPassword = mUserPassword.getText().toString().trim();
        String UserDOB = mUserDOB.getText().toString().trim();
        if (TextUtils.isEmpty(UserName)) {
            mUserName.setError(getResources().getString(R.string.str_first_validation));
            return false;
        }
        if (TextUtils.isEmpty(UserEmailId)) {
            mUserEmailId.setError(getResources().getString(R.string.str_emailId_validation));
            return false;
        } else if (!TextUtils.isEmpty(UserEmailId)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(mUserEmailId.getText().toString()).matches()) {
                mUserEmailId.setError(getResources().getString(R.string.str_emailId_invalid));
                return false;
            }
        }
        if (TextUtils.isEmpty(UserPassword)) {
            mUserPassword.setError(getResources().getString(R.string.str_password_validation));
            return false;
        }
        if (TextUtils.isEmpty(UserDOB)) {
            mUserDOB.setError(getResources().getString(R.string.str_dob_validation));
            return false;
        }

        return true;
    }


}
