package com.aftersapp.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

/**
 * Created by akshay on 19-09-2016.
 */
public class PartyDataDTO extends BaseDTO {

    private long partyId;
    private String title;
    private String desc;
    private double latitude;
    private double longitude;
    private String location;
    private String music;
    private String age;
   // private int interest;
    private int attending;
    private String image;
    private int host;
    private long createdDate;
    private String hostName;
    private int isFavourite;
    private int isLike;
    private String dateOfParty;

    public PartyDataDTO() {
    }

    public PartyDataDTO(long partyId, String title, String desc, double latitude,
                        double longitude, String location, String music, String age,
                        int interest, int attending, String image, int host,
                        long createdDate, String hostName, int isFavourite, int isLike, String dateOfParty) {
        this.partyId = partyId;
        this.title = title;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.music = music;
        this.age = age;
        //this.interest = interest;
        this.attending = attending;
        this.image = image;
        this.host = host;
        this.createdDate = createdDate;
        this.hostName = hostName;
        this.isFavourite = isFavourite;
        this.isLike = isLike;
        this.dateOfParty = dateOfParty;
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

   /* public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }*/

    public int getAttending() {
        return attending;
    }

    public void setAttending(int attending) {
        this.attending = attending;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getDateOfParty() {
        return dateOfParty;
    }

    public void setDateOfParty(String dateOfParty) {
        this.dateOfParty = dateOfParty;
    }

    public static ArrayList<PartyDataDTO> deserializeToArray(String serializedString) {
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
    }
}
