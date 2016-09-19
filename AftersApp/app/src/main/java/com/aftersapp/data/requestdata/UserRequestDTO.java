package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 19-09-2016.
 */
public class UserRequestDTO extends BaseDTO {

    private long userId;
    private String email;

    public UserRequestDTO(long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
