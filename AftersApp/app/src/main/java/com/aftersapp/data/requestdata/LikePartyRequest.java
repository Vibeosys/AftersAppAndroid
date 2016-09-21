package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 20-09-2016.
 */
public class LikePartyRequest extends BaseDTO {

    private long userId;
    private long partyId;

    public LikePartyRequest(long userId, long partyId) {
        this.userId = userId;
        this.partyId = partyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }
}
