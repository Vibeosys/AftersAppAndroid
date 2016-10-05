package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 19-09-2016.
 */
public class GetPartyDTO extends BaseDTO {

    private long userId;
    private double latitude;
    private double longitude;
    private int age;
    private int radiusInKm;
    private String genre;
    private String partyDate;

    public GetPartyDTO(long userId, double latitude, double longitude, int age, int radiusInKm, String genre, String partyDate) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.age = age;
        this.radiusInKm = radiusInKm;
        this.genre = genre;
        this.partyDate = partyDate;
    }

    public GetPartyDTO(long userId, double latitude, double longitude, int age, int radiusInKm) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.age = age;
        this.radiusInKm = radiusInKm;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getRadius() {
        return radiusInKm;
    }

    public void setRadius(int radiusInKm) {
        this.radiusInKm = radiusInKm;
    }

    public String getGenre() {
        return genre.toLowerCase();
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPartyDate() {
        return partyDate;
    }

    public void setPartyDate(String partyDate) {
        this.partyDate = partyDate;
    }
}
