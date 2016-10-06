package com.aftersapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by akshay on 05-10-2016.
 */
public class MapDirectionData implements Serializable {

    private double sourceLatitude;
    private double sourceLongitude;
    private double destinationLatitude;
    private double destinationLongitude;

    public MapDirectionData(double sourceLatitude, double sourceLongitude) {
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
    }

    public MapDirectionData(double sourceLatitude, double sourceLongitude, double destinationLatitude, double destinationLongitude) {
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    public double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
}
