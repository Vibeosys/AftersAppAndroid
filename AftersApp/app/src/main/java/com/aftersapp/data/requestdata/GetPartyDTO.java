package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 19-09-2016.
 */
public class GetPartyDTO extends BaseDTO {

    private long userId;
    private double latitude;
    private double longitude;

    public GetPartyDTO(long userId, double latitude, double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
