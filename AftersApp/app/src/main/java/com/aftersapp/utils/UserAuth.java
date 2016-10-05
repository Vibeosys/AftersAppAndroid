package com.aftersapp.utils;

import android.content.Context;
import android.content.Intent;

import com.aftersapp.activities.LoginActivity;
import com.aftersapp.data.UserDTO;

/**
 * Created by akshay on 20-09-2016.
 */
public class UserAuth {

    public static boolean isUserLoggedIn(Context context, String userName, String password) {
        if (password == null || password == "" || userName == null || userName == "") {
            Intent theLoginIntent = new Intent(context, LoginActivity.class);
            //theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            theLoginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(theLoginIntent);
            return false;
        }
        return true;
    }


    public static boolean isUserLoggedIn() {
        long theUserId = SessionManager.Instance().getUserId();
        String theUserEmail = SessionManager.Instance().getEmail();
        //String theUserPhotoURL = SessionManager.Instance().getUserPhotoUrl();

        if (theUserId == 0 || theUserEmail == null || theUserEmail == "") {
            return false;
        }
        return true;
    }

    public void saveAuthenticationInfo(UserDTO userInfo, final Context context) {
        if (userInfo == null)
            return;

        if (userInfo.getEmail() == null || userInfo.getEmail() == "" ||
                userInfo.getName() == null || userInfo.getName() == "")
            return;

        SessionManager theSessionManager = SessionManager.getInstance(context);
        theSessionManager.setUserId(userInfo.getUserId());
        theSessionManager.setName(userInfo.getName());
        theSessionManager.setEmail(userInfo.getEmail());
        theSessionManager.setEmail2(userInfo.getEmail2());
        theSessionManager.setPhone(userInfo.getPhone());
        theSessionManager.setGender(userInfo.getGender());
        theSessionManager.setProfileImg(userInfo.getProfImage());
        theSessionManager.setDob(userInfo.getDob());
        theSessionManager.setToken(userInfo.getToken());
        theSessionManager.setEmailNotify(userInfo.getEmailNotify());
    }

    public static boolean CleanAuthenticationInfo() {

        SessionManager theSessionManager = SessionManager.Instance();
        theSessionManager.setUserId(0);
        theSessionManager.setName(null);
        theSessionManager.setEmail(null);
        theSessionManager.setEmail2(null);
        theSessionManager.setPhone(null);
        theSessionManager.setGender(null);
        theSessionManager.setProfileImg(null);
        theSessionManager.setDob(null);
        theSessionManager.setToken(null);
        theSessionManager.setEmailNotify(0);
        theSessionManager.setDisclaimerVal(0);
        return true;
    }
}
