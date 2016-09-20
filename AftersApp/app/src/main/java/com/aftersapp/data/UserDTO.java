package com.aftersapp.data;

/**
 * Created by akshay on 20-09-2016.
 */
public class UserDTO {

    private long mUserId;
    private String mName;
    private String mEmail;
    private String mEmail2;
    private String mPhone;
    private String mGender;
    private String mProfImage;
    private String mDob;
    private String mToken;
    private int mEmailNotify;

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getEmail2() {
        return mEmail2;
    }

    public void setEmail2(String mEmail2) {
        this.mEmail2 = mEmail2;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public String getProfImage() {
        return mProfImage;
    }

    public void setProfImage(String mProfImage) {
        this.mProfImage = mProfImage;
    }

    public String getDob() {
        return mDob;
    }

    public void setDob(String mDob) {
        this.mDob = mDob;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public int getEmailNotify() {
        return mEmailNotify;
    }

    public void setEmailNotify(int mEmailNotify) {
        this.mEmailNotify = mEmailNotify;
    }
}
