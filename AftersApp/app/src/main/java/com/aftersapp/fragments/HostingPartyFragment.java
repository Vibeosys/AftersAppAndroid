package com.aftersapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.aftersapp.R;
import com.aftersapp.data.PartyDataDTO;

import java.util.ArrayList;

/**
 * Created by akshay on 04-10-2016.
 */
public class HostingPartyFragment extends FindPartyFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.str_err_no_host_party);
    }

    @Override
    protected ArrayList<PartyDataDTO> setSortedList(ArrayList<PartyDataDTO> allParties) {
        ArrayList<PartyDataDTO> sortedList = new ArrayList<PartyDataDTO>();
        for (PartyDataDTO party : allParties) {
            int hostId = party.getHost();
            if (mSessionManager.getUserId() == hostId) {
                sortedList.add(party);
            } else {

            }

        }
        return sortedList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
