package com.aftersapp.data.requestdata;

import com.aftersapp.data.BaseDTO;

/**
 * Created by akshay on 05-10-2016.
 */
public class DeletePartyDTO extends BaseDTO {
    private long partyId;

    public DeletePartyDTO(long partyId) {
        this.partyId = partyId;
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }
}
