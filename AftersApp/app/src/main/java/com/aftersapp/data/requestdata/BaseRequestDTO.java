package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 19-09-2016.
 */
public class BaseRequestDTO extends BaseDTO {

    private UserRequestDTO user;
    private String data;

    public BaseRequestDTO() {
    }

    public BaseRequestDTO(UserRequestDTO user, String data) {
        this.user = user;
        this.data = data;
    }

    public UserRequestDTO getUser() {
        return user;
    }

    public void setUser(UserRequestDTO user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
