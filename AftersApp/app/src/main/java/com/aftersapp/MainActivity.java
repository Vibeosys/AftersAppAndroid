package com.aftersapp;

import android.*;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aftersapp.activities.BaseActivity;
import com.aftersapp.activities.LoginActivity;
import com.aftersapp.activities.LogoutActivity;
import com.aftersapp.fragments.AllPartiesFragment;
import com.aftersapp.fragments.AttendingPartiesFragment;
import com.aftersapp.fragments.ChatsUsersList;
import com.aftersapp.fragments.EditMyProfileFragment;
import com.aftersapp.fragments.FavPartiesFragment;
import com.aftersapp.fragments.FilterFragment;
import com.aftersapp.fragments.FindPartyFragment;
import com.aftersapp.fragments.HomeFragment;
import com.aftersapp.fragments.HostPartyFragment;
import com.aftersapp.fragments.HostingPartyFragment;
import com.aftersapp.fragments.PurchaseFragment;
import com.aftersapp.fragments.UserListFragment;
import com.aftersapp.fragments.ViewProfileFragment;
import com.aftersapp.helper.ChatHelper;
import com.aftersapp.helper.DataHolder;
import com.aftersapp.interfaces.GcmConsts;
import com.aftersapp.services.AdService;
import com.aftersapp.utils.AppConstants;
import com.aftersapp.utils.UserAuth;
import com.aftersapp.utils.qbutils.SharedPreferencesUtil;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBEntity;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout mHomeLay, mSearchLay, mHostLay, mMoreLay;
    private static final String HOME_FRAGMENT = "home";
    private static final String SEARCH_FRAGMENT = "search";
    private static final String HOST_FRAGMENT = "host";
    private static final String MORE_FRAGMENT = "more";
    private static final String USER_FRAGMENT = "user";
    private static final String USER_PROFILE = "viewProfile";
    private static final String PURCHASE_FRAGMENT = "purchase";
    private static final String USER_LIST_FRAGEMNT = "user_list";
    public static InterstitialAd mInterstitialAd;
    private CircleImageView profileImg;
    private TextView mNavigationUserEmailId, mNavigationUserName;
    public static Handler UIHandler;
    private Intent iservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        iservice = new Intent(getApplicationContext(), AdService.class);
        if (!UserAuth.isUserLoggedIn()) {
            // finish();
            callLogin();
            return;
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHomeLay = (LinearLayout) findViewById(R.id.homeLay);
        mSearchLay = (LinearLayout) findViewById(R.id.searchLay);
        mHostLay = (LinearLayout) findViewById(R.id.hostLay);
        mMoreLay = (LinearLayout) findViewById(R.id.moreLay);

        View headerView = navigationView.getHeaderView(0);
        mNavigationUserEmailId = (TextView) headerView.findViewById(R.id.userEmailId);
        mNavigationUserName = (TextView) headerView.findViewById(R.id.userName);
        profileImg = (CircleImageView) headerView.findViewById(R.id.imageView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                String mImageUri = mSessionManager.getProfImg();
                mNavigationUserEmailId.setText("" + mSessionManager.getEmail2());
                mNavigationUserName.setText("" + mSessionManager.getName());
                if (!TextUtils.isEmpty(mImageUri)) {
                    String stringImg = mSessionManager.getProfImg();
                    if (mImageUri.equals("null")) {
                        profileImg.setImageResource(R.drawable.avatar_profile);
                    } else {
                        DownloadImage downloadImage = new DownloadImage();
                        downloadImage.execute(mImageUri);
                    }

                } else if (TextUtils.isEmpty(mSessionManager.getProfImg())) {
                    profileImg.setImageResource(R.drawable.avatar_profile);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mHomeLay.setOnClickListener(this);
        mSearchLay.setOnClickListener(this);
        mHostLay.setOnClickListener(this);
        mMoreLay.setOnClickListener(this);
        setUpFragment(R.id.homeLay);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstial_ad_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // super.onAdLoaded();
                showIntestititalCase();
            }
        });
       /* AdRequest adRequest = new AdRequest.Builder().addTestDevice("1C22DEC8AEF4249E83143364E2E5AC32").build();
        mInterstitialAd.loadAd(adRequest);*/
        if (mSessionManager.getIsPurchased() == AppConstants.ITEM_NOT_PURCHASED) {
            if (!isMyServiceRunning(AdService.class))
                startService(iservice);
        } else if (mSessionManager.getIsPurchased() == AppConstants.ITEM_PURCHASED) {
            if (isMyServiceRunning(AdService.class))
                stopService(iservice);
        }

        if (mSessionManager.getIsPurchased() == AppConstants.ITEM_PURCHASED) {
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_main_drawer);// drawer for subscribers
            TextView view = (TextView) navigationView.getMenu().findItem(R.id.nav_messages).getActionView();
            int messageCount = mSessionManager.getMessageCount();
            view.setText(messageCount > 0 ? messageCount > 99 ? "" + messageCount + "+" : String.valueOf(messageCount) : null);
        } else {
            navigationView.getMenu().clear(); //clear old inflated items.
            navigationView.inflateMenu(R.menu.activity_unsubscribe_drawer);
        }
        if (getIntent().getExtras() != null) {
            ChatsUsersList userListFragment = new ChatsUsersList();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, userListFragment, USER_FRAGMENT).commit();
        }
    }

    public void callLogin() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void loadAd(Runnable runnable) {
        UIHandler.post(runnable);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT).commit();
        } else if (id == R.id.nav_profile) {
            /*EditMyProfileFragment editMyProfileFragment = new EditMyProfileFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, editMyProfileFragment, MORE_FRAGMENT).commit();*/
            ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, viewProfileFragment, MORE_FRAGMENT).commit();

        } else if (id == R.id.nav_messages) {
            ChatsUsersList userListFragment = new ChatsUsersList();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, userListFragment, USER_FRAGMENT).commit();

        } else if (id == R.id.nav_removeAd) {

            PurchaseFragment purchaseFragment = new PurchaseFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, purchaseFragment, PURCHASE_FRAGMENT).commit();
        } else if (id == R.id.nav_hosting) {
            FindPartyFragment partyFragment = new HostingPartyFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, partyFragment, SEARCH_FRAGMENT).commit();
        } else if (id == R.id.nav_fav) {

            FindPartyFragment partyFragment = new FavPartiesFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, partyFragment, SEARCH_FRAGMENT).commit();
        } else if (id == R.id.nav_attending) {

            FindPartyFragment partyFragment = new AttendingPartiesFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, partyFragment, SEARCH_FRAGMENT).commit();
        } else if (id == R.id.nav_logout) {

            logoutFromQb();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutFromQb() {

        /*QBUsers.signOut(new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });*/
        AsyncSignOut signOut = new AsyncSignOut();
        signOut.execute();

    }

    private void setUpFragment(int i) {
        switch (i) {
            case R.id.homeLay:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT).commit();
                break;
            case R.id.searchLay:
                FindPartyFragment partyFragment = new AllPartiesFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, partyFragment, SEARCH_FRAGMENT).commit();
                break;
            case R.id.hostLay:
                HostPartyFragment hostPartyFragment = new HostPartyFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, hostPartyFragment, HOST_FRAGMENT).commit();
                break;
            case R.id.moreLay:
                ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame_lay, viewProfileFragment, USER_PROFILE).commit();


                break;
            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        setUpFragment(id);
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
                bitmap = null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                profileImg.setImageBitmap(result);
            } else {
                profileImg.setImageResource(R.drawable.avatar_profile);
            }
        }
    }


    public void onStartNewChatClick(View view) {
        UserListFragment partyFragment = new UserListFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_frame_lay, partyFragment, USER_LIST_FRAGEMNT).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMyServiceRunning(AdService.class))
            stopService(iservice);
        try {
            ChatHelper.getInstance().logout();
        } catch (Exception e) {
        }
    }

    private class AsyncSignOut extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                QBUsers.signOut();
            } catch (QBResponseException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferencesUtil.removeQbUser();
            DataHolder.getInstance().setSignInQbUser(null);
            LoginActivity.LogoutFacebook();
            UserAuth.CleanAuthenticationInfo();
            Intent logout = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(logout);
            finish();
        }
    }

    public void showIntestititalCase() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
