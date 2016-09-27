package com.aftersapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
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
import com.aftersapp.helper.DataHolder;
import com.aftersapp.utils.ServerRequestConstants;
import com.aftersapp.utils.ServerSyncManager;
import com.aftersapp.utils.UserAuth;
import com.aftersapp.utils.qbutils.SharedPreferencesUtil;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Arrays;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        ServerSyncManager.OnSuccessResultReceived,
        ServerSyncManager.OnErrorResultReceived, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, QBEntityCallback<QBUser> {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView imgFb, imgGPlus;
    CallbackManager callbackManager;
    protected GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
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
        googlePlusAPIInit();
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
        if (requestCode == RC_SIGN_IN) {
            // mSignInClicked = false;
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getProfileInformation(result);

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
        parameters.putString("fields", "id,name,birthday,picture.type(large){url,height,width,is_silhouette},gender,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void callToRegister(String name, String email, String gender,
                                String profileImg, String dob, String token) {
        Log.d(TAG, "## email" + email + " first Name" + name +
                " Gender " + gender + " picture url" + profileImg);
        progressDialog.show();
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
                progressDialog.dismiss();
                break;

        }
    }

    @Override
    public void onDataErrorReceived(int errorCode, String errorMessage, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_REGISTER:
                Log.d(TAG, "##Volley Data error " + errorMessage);
                progressDialog.dismiss();
                break;
        }
    }

    @Override
    public void onResultReceived(@NonNull String data, int requestToken) {
        switch (requestToken) {
            case ServerRequestConstants.REQUEST_REGISTER:
                progressDialog.dismiss();
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
                registerDataOnQb();

                break;
        }
    }

    private void registerDataOnQb() {
        progressDialog.show();
        QBUser qbUser = new QBUser();
        qbUser.setFullName(name);
        qbUser.setEmail(email);
        qbUser.setLogin(email);
        qbUser.setPassword(email + mSessionManager.getUserId());
        qbUser.setId(Integer.parseInt(String.valueOf(mSessionManager.getUserId())));
        qbUser.setExternalId(String.valueOf(mSessionManager.getUserId()));
        QBUsers.signUpSignInTask(qbUser, this);
    }

    public void getAccountPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                ACCOUNT_PERMISSION_CODE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // getProfileInformation();
        Log.d("TAG", "LOGIN");


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCOUNT_PERMISSION_CODE && grantResults[0] == 0) {
            signInGooglePluse();


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "User denied permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void googlePlusAPIInit() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, LoginActivity.this)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void signInGooglePluse() {
        if (mGoogleApiClient != null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        }

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

    private void getProfileInformation(GoogleSignInResult result) {
        try {
            if (result.isSuccess()) {

                GoogleSignInAccount acct = result.getSignInAccount();
                email = acct.getEmail();
                Uri Img = acct.getPhotoUrl();
                String profileImg = Img.toString();
                String id = acct.getId();
                name = acct.getDisplayName();
                callToRegister(name, email, "Male", profileImg, "", id);


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

    @Override
    public void onSuccess(QBUser qbUser, Bundle bundle) {
        progressDialog.dismiss();
        SharedPreferencesUtil.saveQbUser(qbUser);
        DataHolder.getInstance().addQbUser(qbUser);
        DataHolder.getInstance().setSignInQbUser(qbUser);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onError(QBResponseException e) {
        progressDialog.dismiss();
        signIn();
    }

    public void signIn() {

        progressDialog.show();

        QBUser qbUser = new QBUser(email, email + mSessionManager.getUserId());
        QBUsers.signIn(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                progressDialog.dismiss();
                DataHolder.getInstance().setSignInQbUser(qbUser);
                signInChat();
            }

            @Override
            public void onError(QBResponseException errors) {
                progressDialog.dismiss();
                signIn();
            }
        });
    }

    public void signInChat() {
        progressDialog.show();
        final QBChatService chatService = QBChatService.getInstance();

        final QBUser user = new QBUser(email, email + mSessionManager.getUserId());
        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success, login to chat

                user.setId(session.getUserId());

                chatService.login(user, new QBEntityCallback() {

                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException errors) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }
}