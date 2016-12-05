package com.aftersapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aftersapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends BaseActivity implements View.OnClickListener {

    EditText txtEmail;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnOk:
                sendEmail();
                break;
        }
    }

    private void sendEmail() {
        String email = txtEmail.getText().toString();
        boolean check = false;
        View focusView = null;
        if (TextUtils.isEmpty(email)) {
            check = true;
            focusView = txtEmail;
            txtEmail.setError(getResources().getString(R.string.str_emailId_validation));
        } else if (!isValidEmail(email)) {
            check = true;
            focusView = txtEmail;
            txtEmail.setError(getResources().getString(R.string.str_emailId_invalid));
        }
        if (check) {
            focusView.requestFocus();
        } else {
            finish();
        }
    }


    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
