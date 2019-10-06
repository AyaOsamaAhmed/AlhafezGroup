package com.alhafezegypt.app;

/**
 * Created by shady on 11/9/16.
 */
public class LocationData {

    private String latitude;
    private String longitude;
    
    public LocationData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
