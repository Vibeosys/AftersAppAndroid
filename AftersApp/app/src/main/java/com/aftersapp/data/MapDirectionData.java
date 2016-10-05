package com.aftersapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by akshay on 05-10-2016.
 */
public class MapDirectionData implements Serializable {

    private LatLng sourceLatLng;
    private LatLng destinationLatLng;

    public MapDirectionData(LatLng sourceLatLng) {
        this.sourceLatLng = sourceLatLng;
    }

    public MapDirectionData(LatLng sourceLatLng, LatLng destinationLatLng) {
        this.sourceLatLng = sourceLatLng;
        this.destinationLatLng = destinationLatLng;
    }

    public LatLng getSourceLatLng() {
        return sourceLatLng;
    }

    public void setSourceLatLng(LatLng sourceLatLng) {
        this.sourceLatLng = sourceLatLng;
    }

    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }
}
