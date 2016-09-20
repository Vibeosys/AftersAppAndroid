package com.aftersapp.data.responsedata;

import com.aftersapp.data.BaseDTO;

import java.util.Date;

/**
 * Created by shrinivas on 20-09-2016.
 */
public class HostPartyDTO extends BaseDTO{
    String title;
    String desc;
    double latitude;
    double longitude;
    String location;
    String music;
    String age;
    String interest;
    String attending;
    String image;
    String host;
    String pdate;

    public HostPartyDTO(String title, String desc, double latitude, double longitude, String location,
                        String music, String age, String interest, String attending, String image,
                        String host, String pdate) {
        this.title = title;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.music = music;
        this.age = age;
        this.interest = interest;
        this.attending = attending;
        this.image = image;
        this.host = host;
        this.pdate = pdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getAttending() {
        return attending;
    }

    public void setAttending(String attending) {
        this.attending = attending;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }
}