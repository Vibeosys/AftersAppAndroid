package com.aftersapp.data;

/**
 * Created by akshay on 16-09-2016.
 */
public class PartyData {

    private long mId;
    private String mPartyName;
    private String mPartyDesc;
    private int mAge;
    private int mAttending;
    private PartyLocation mPartyLocation;

    public PartyData(long mId, String mPartyName, String mPartyDesc, int mAge, int mAttending, PartyLocation partyLocation) {
        this.mId = mId;
        this.mPartyName = mPartyName;
        this.mPartyDesc = mPartyDesc;
        this.mAge = mAge;
        this.mAttending = mAttending;
        this.mPartyLocation = partyLocation;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getPartyName() {
        return mPartyName;
    }

    public void setPartyName(String mPartyTitle) {
        this.mPartyName = mPartyTitle;
    }

    public String getPartyDesc() {
        return mPartyDesc;
    }

    public void setPartyDesc(String mPartyDesc) {
        this.mPartyDesc = mPartyDesc;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int mAge) {
        this.mAge = mAge;
    }

    public int getAttending() {
        return mAttending;
    }

    public void setAttending(int mAttending) {
        this.mAttending = mAttending;
    }

    public PartyLocation getPartyLocation() {
        return mPartyLocation;
    }

    public void setPartyLocation(PartyLocation mPartyLocation) {
        this.mPartyLocation = mPartyLocation;
    }
}
