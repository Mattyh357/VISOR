package com.matt.visor.GoogleMapsAPI;

import com.google.android.gms.maps.model.LatLng;

public class Waypoint {
    public String instruction;
    public String distanceText;
    public int distanceValue; // in meters
    public String durationText;
    public int durationValue; // in seconds
    public LatLng startLocation;
    public LatLng endLocation;
    public String maneuver; // optional


}

