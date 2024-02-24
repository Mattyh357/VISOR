package com.matt.visor.GoogleMap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Callback interface for handling the results from the Google Directions API.
 */
public interface DirectionsCallback {

    /**
     * Called when directions are successfully fetched.
     *
     * @param steps The list of steps for the route.
     * @param polylineRoute The polyline route as a list of LatLng points.
     */
    void onSuccess(List<Step> steps, List<LatLng> polylineRoute);

    /**
     * Called when there is a failure in fetching directions.
     *
     * @param response The error response from the server.
     */
    void onFail(String response);

    /**
     * Called when an error occurs during the process.
     *
     * @param e The exception thrown during the process.
     */
    void onError(Exception e);

}
