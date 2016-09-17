package com.aftersapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by akshay on 16-09-2016.
 */
public class PartyLocation implements ClusterItem {

    private final LatLng mPosition;

    public PartyLocation(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return this.mPosition;
    }
}
