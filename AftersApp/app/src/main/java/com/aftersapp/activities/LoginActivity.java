package com.aftersapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.aftersapp.MainActivity;
import com.aftersapp.R;
import com.aftersapp.data.UserDTO;
import com.aftersapp.data.requestdata.BaseRequestDTO;
import com.aftersapp.data.requestdata.RegisterUserDataDTO;
import com.aftersapp.data.responsedata.RegisterResponseData;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.aftersapp.utils.UserAuth;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        ServerSyncManager.OnSuccessResultReceived,
        ServerSyncManager.OnErrorResultReceived, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView imgFb, imgGPlus;
    CallbackManager callbackManager;
    protected GoogleApiClient mGoogleApiClient;
    private int ACCOUNT_PERMISSION_CODE = 14;
    private static int RC_SIGN_IN = 400;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private String email = null;
    private String name = null;
    private String gender = null;
    private String token = null;
    private String profileImg = null;
    private String dob = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        imgFb = (ImageView) findViewById(R.id.fbImg);
        imgGPlus = (ImageView) findViewById(R.id.gpImg);
        imgFb.setOnClickListener(this);
        imgGPlus.setOnClickListener(this);
        mServerSyncManager.setOnStringErrorReceived(this);
        mServerSyncManager.setOnStringResultReceived(this);
        //Facebook code
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getTheDetails(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fbImg:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email,public_profile,user_birthday"));
                break;
            case R.id.gpImg:
                getAccountPermission();
                break;
        }
       /* startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();*/
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RC_SIGN_IN) {
            mSignInClicked = false;
        } else {
            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void getTheDetails(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        try {
                            email = object.getString("email");
                            name = object.getString("name");
                            gender = object.getString("gender");
                            AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
                            token = null;
                            if (fbAccessToken != null) {
                                token = fbAccessToken.getToken();
                            }
                            JSONObject picture = object.getJSONObject("picture");

                            if (picture != null) {
                                JSONObject pictureData = picture.getJSONObject("data");
                                if (pictureData != null) {
                                    profileImg = pictureData.getString("url");
                                }
                            }
                            dob = object.getString("birthday");
                            mSessionManager.setToken(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        callToRegister(name, email, gender, profileImg, dob, token);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,birthday,picture{url,height,width,is_silhouette},gender,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void callToRegister(String name, String email, String gender,
                                String profileImg, String dob, String token) {
        Log.d(TAG, "## email" + email + " first Name" + name +
                " Gender " + gender + " picture url" + profileImg);
        RegisterUserDataDTO registerUserDataDTO = new RegisterUserDataDTO(name, email,
                "1234567890", gender, profileImg, dob, token, 1);
        Gson gson = new Gson();
        String serializedJsonString = gson.toJson(registerUserDataDTO);
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setData(serializedJsonString);
        mServerSyncManager.uploadDataToServer(ServerRequestConstants.REQUEST_REGISTER,
                mSessionManager.registerUser(), baseRequestDTO);
    }

    @Override
    public void onVolleyErrorReceived(@NonNull VolleyError error, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_REGISTER:
                Log.e(TAG, "##Volley Server error " + error.toString());
                break;

        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_REGISTER:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_REGISTER:
                RegisterResponseData registerResponseData = RegisterResponseData.deserializeJson(data);
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(registerResponseData.getUserId());
                userDTO.setName(name);
                userDTO.setEmail(email);
                userDTO.setEmail2(email);
                userDTO.setPhone("1234567890");
                userDTO.setGender(gender);
                userDTO.setProfImage(profileImg);
                userDTO.setDob(dob);
                userDTO.setToken(token);
                userDTO.setEmailNotify(1);

                UserAuth userAuth = new UserAuth();
                userAuth.saveAuthenticationInfo(userDTO, getApplicationContext());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }

    public void getAccountPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                ACCOUNT_PERMISSION_CODE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getProfileInformation();
        Log.d("TAG", "LOGIN");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                // GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
                return;
            } catch (Exception e) {
                mGoogleApiClient.connect();
            }

        }
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCOUNT_PERMISSION_CODE && grantResults[0] == 0) {
            googlePlusAPIInit();
            if (!mGoogleApiClient.isConnecting()) {
                if (mGoogleApiClient.isConnected()) {
                    mSignInClicked = true;
                    resolveSignInError();
                } else {
                    mGoogleApiClient.connect();
                    if (mGoogleApiClient.isConnected()) {
                        mSignInClicked = true;
                        resolveSignInError();
                    }
                }
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "User denied permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void googlePlusAPIInit() {
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addConnectionCallbacks(LoginActivity.this)
                .addOnConnectionFailedListener(LoginActivity.this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    private void resolveSignInError() {

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                name = currentPerson.getDisplayName();
                profileImg = currentPerson.getImage().getUrl();
                //personGooglePlusProfile = currentPerson.getUrl();
                token = currentPerson.getId();
                int iGender = currentPerson.getGender();
                gender = iGender == Person.Gender.MALE ? "Male" :
                        iGender == Person.Gender.FEMALE ? "Female" : "Other";
                dob = currentPerson.getBirthday();
                callToRegister(name, email, gender, profileImg, dob, token);
            } else {
                Log.e("user profile is null", "profile is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LogoutFacebook() {
        try {
            LoginManager.getInstance().logOut();
            Log.d("FBLOGIN", "Log out");

        } catch (FacebookException e) {
            e.printStackTrace();
            Log.d(TAG, "Facebook logout exception");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }
}