package com.aftersapp.data.requestdata;

/**
 * Created by shrinivas on 22-09-2016.
 */
public class UpdateProfileDTO {
    long userId;
    String name;
    String email;
    String email2;
    String phone;
    String gender;
    String profileImage;
    String dob;
    String token;
    int emailNotify;

    public UpdateProfileDTO(long userId, String name, String email, String email2, String phone,
                            String gender, String profileImage, String dob, String token,
                            int emailNotify) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.email2 = email2;
        this.phone = phone;
        this.gender = gender;
        this.profileImage = profileImage;
        this.dob = dob;
        this.token = token;
        this.emailNotify = emailNotify;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
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
