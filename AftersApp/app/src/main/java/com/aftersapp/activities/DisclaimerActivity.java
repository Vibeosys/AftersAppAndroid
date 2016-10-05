package com.aftersapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aftersapp.MainActivity;
import com.aftersapp.R;
import com.aftersapp.utils.AppConstants;
import com.aftersapp.utils.SessionManager;

public class DisclaimerActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mAboveEighteen,mBelowEighteen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_disclaimer);
        mAboveEighteen =(ImageView) findViewById(R.id.above_eighteen);
        mBelowEighteen =(ImageView) findViewById(R.id.under_eighteen);
       // mSessionManager.setDisclaimerVal(0);
     //   int testVal = mSessionManager.getDisclaimerVal();
        Log.d("TAG","TAG");
        if(mSessionManager.getDisclaimerVal()==1)
        {
            Intent loginActivity = new Intent(DisclaimerActivity.this, MainActivity.class);
            startActivity(loginActivity);
            finish();
        }
        mAboveEighteen.setOnClickListener(this);
        mBelowEighteen.setOnClickListener(this);
      //  int testVal = mSessionManager.getDisclaimerVal();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.above_eighteen:
                mSessionManager.setDisclaimerVal(1);
                Intent loginActivity = new Intent(DisclaimerActivity.this, MainActivity.class);
                startActivity(loginActivity);
                finish();
                break;
            case R.id.under_eighteen:
                mSessionManager.setDisclaimerVal(0);
                finish();
                break;
        }
    }
}
