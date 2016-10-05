package com.aftersapp.data.responsedata;

import com.aftersapp.data.BaseDTO;

import java.util.Date;

/**
 * Created by shrinivas on 20-09-2016.
 */
public class HostPartyDTO extends BaseDTO {
    String title;
    String desc;
    double latitude;
    double longitude;
    String location;
    String music;
    int age;
    String interest;
    String attending;
    String image;
    long host;
    long pdate;
    String partyDate;

    public HostPartyDTO(String title, String desc, double latitude, double longitude, String location,
                        String music, int age, String interest, String attending, String image,
                        long host, long pdate, String partyDate) {
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
        this.partyDate = partyDate;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public long getHost() {
        return host;
    }

    public void setHost(long host) {
        this.host = host;
    }

    public long getPdate() {
        return pdate;
    }

    public void setPdate(long pdate) {
        this.pdate = pdate;
    }

    public String getPartyDate() {
        return partyDate;
    }

    public void setPartyDate(String partyDate) {
        this.partyDate = partyDate;
    }
}
