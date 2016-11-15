package com.aftersapp.data.responsedata;

import android.util.Log;

import com.aftersapp.data.BaseDTO;
import com.aftersapp.data.PartyDataDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

/**
 * Created by akshay on 19-09-2016.
 */
public class TempDataDTO extends BaseDTO {

    private long partyId;
    private String title;
    private String desc;
    private String latitude;
    private String longitude;
    private String location;
    private String music;
    private String age;
    private String interest;
    private String attending;
    private String image;
    private String host;
    private String createdDate;
    private String hostName;
    private String isFavourite;
    private String isLike;
    private String dateOfParty;

    public TempDataDTO() {
    }


    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getDateOfParty() {
        return dateOfParty;
    }

    public void setDateOfParty(String dateOfParty) {
        this.dateOfParty = dateOfParty;
    }

    /*public static ArrayList<PartyDataDTO> deserializeToArray(String serializedString) {
        Gson gson = new Gson();
        ArrayList<PartyDataDTO> partyDataDTOs = null;
        try {
            PartyDataDTO[] deserializeObject = gson.fromJson(serializedString, PartyDataDTO[].class);
            partyDataDTOs = new ArrayList<>();
            for (PartyDataDTO signalDTO : deserializeObject) {
                partyDataDTOs.add(signalDTO);
            }
        } catch (JsonSyntaxException e) {
            Log.e("deserialize", "Response Party DTO error" + e.toString());
        }


        return partyDataDTOs;
    }*/
}
