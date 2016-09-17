package com.aftersapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aftersapp.MainActivity;
import com.aftersapp.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgFb, imgGPlus, imgTwitter;

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
        imgTwitter = (ImageView) findViewById(R.id.twitterImg);

        imgFb.setOnClickListener(this);
        imgGPlus.setOnClickListener(this);
        imgGPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}