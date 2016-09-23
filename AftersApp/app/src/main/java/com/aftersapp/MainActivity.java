package com.aftersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
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

import com.aftersapp.activities.BaseActivity;
import com.aftersapp.activities.LoginActivity;
import com.aftersapp.activities.LogoutActivity;
import com.aftersapp.fragments.EditMyProfileFragment;
import com.aftersapp.fragments.FilterFragment;
import com.aftersapp.fragments.FindPartyFragment;
import com.aftersapp.fragments.HomeFragment;
import com.aftersapp.fragments.HostPartyFragment;
import com.aftersapp.fragments.UserListFragment;
import com.aftersapp.fragments.ViewProfileFragment;
import com.aftersapp.helper.DataHolder;
import com.aftersapp.utils.UserAuth;
import com.quickblox.users.model.QBUser;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private LinearLayout mHomeLay, mSearchLay, mHostLay, mMoreLay;
    private static final String HOME_FRAGMENT = "home";
    private static final String SEARCH_FRAGMENT = "search";
    private static final String HOST_FRAGMENT = "host";
    private static final String MORE_FRAGMENT = "more";
    private static final String USER_FRAGMENT = "user";
    private CircleImageView profileImg;
    private TextView mNavigationUserEmailId, mNavigationUserName;

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
        if (!UserAuth.isUserLoggedIn()) {
            // finish();
            callLogin();
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHomeLay = (LinearLayout) findViewById(R.id.homeLay);
        mSearchLay = (LinearLayout) findViewById(R.id.searchLay);
        mHostLay = (LinearLayout) findViewById(R.id.hostLay);
        mMoreLay = (LinearLayout) findViewById(R.id.moreLay);

        View headerView = navigationView.getHeaderView(0);
        mNavigationUserEmailId = (TextView) headerView.findViewById(R.id.userEmailId);
        mNavigationUserName = (TextView) headerView.findViewById(R.id.userName);
        String mImageUri = mSessionManager.getProfImg();
        profileImg = (CircleImageView) headerView.findViewById(R.id.imageView);
        mNavigationUserEmailId.setText("" + mSessionManager.getEmail());
        mNavigationUserName.setText("" + mSessionManager.getName());

        if (!TextUtils.isEmpty(mImageUri)) {
            DownloadImage downloadImage = new DownloadImage();
            downloadImage.execute(mImageUri);
        }
        mHomeLay.setOnClickListener(this);
        mSearchLay.setOnClickListener(this);
        mHostLay.setOnClickListener(this);
        mMoreLay.setOnClickListener(this);
        setUpFragment(R.id.homeLay);
    }

    public void callLogin() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            FilterFragment filterFragment = new FilterFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, filterFragment, MORE_FRAGMENT).commit();
            return true;
        }

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

        }  else if (id == R.id.nav_messages) {
            UserListFragment userListFragment = new UserListFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_frame_lay, userListFragment, USER_FRAGMENT).commit();

        }  else if (id == R.id.nav_logout) {
            DataHolder.getInstance().setSignInQbUser(null);
            LoginActivity.LogoutFacebook();
            UserAuth.CleanAuthenticationInfo();
            Intent logout = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(logout);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpFragment(int i) {

        switch (i) {
            case R.id.homeLay:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, homeFragment, HOME_FRAGMENT).commit();
                break;
            case R.id.searchLay:
                FindPartyFragment partyFragment = new FindPartyFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, partyFragment, SEARCH_FRAGMENT).commit();
                break;
            case R.id.hostLay:
                HostPartyFragment hostPartyFragment = new HostPartyFragment();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_frame_lay, hostPartyFragment, HOST_FRAGMENT).commit();
                break;
            case R.id.moreLay:


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
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            profileImg.setImageBitmap(result);
        }
    }
}
