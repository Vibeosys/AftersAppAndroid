package com.aftersapp.data.requestdata;

/**
 * Created by akshay on 03-12-2016.
 */
public class UserLoginRequestDTO {

    private String email;
    private String password;

    public UserLoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
