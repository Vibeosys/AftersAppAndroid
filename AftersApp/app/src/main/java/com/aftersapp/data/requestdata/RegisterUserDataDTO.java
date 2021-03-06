package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 21-09-2016.
 */
public class RegisterUserDataDTO extends BaseDTO {

    private String name;
    private String email;
    private String phone;
    private String gender;
    private String profileImage;
    private String dob;
    private String token;
    private int emailNotify;

    public RegisterUserDataDTO(String name, String email,
                               String phone, String gender, String profileImage, String dob,
                               String token, int emailNotify) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.profileImage = profileImage;
        this.dob = dob;
        this.token = token;
        this.emailNotify = emailNotify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getEmailNotify() {
        return emailNotify;
    }

    public void setEmailNotify(int emailNotify) {
        this.emailNotify = emailNotify;
    }
}
