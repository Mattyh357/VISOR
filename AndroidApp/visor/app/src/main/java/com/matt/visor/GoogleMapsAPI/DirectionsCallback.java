package com.matt.visor.GoogleMapsAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DirectionsCallback {

    void onSuccess(List<Waypoint> waypoints, List<LatLng> polylineRoute);
    void onFail(String response);
    void onError(Exception e);

}
