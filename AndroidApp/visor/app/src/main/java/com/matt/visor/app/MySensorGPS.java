/**
 * This class is part of the V.I.S.O.R app.
 * Represents a GPS sensor, handling location updates and status.
 * Extends MySensor to provide GPS-specific functionality.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.app;

import android.app.Activity;
import android.location.Location;
import android.os.Looper;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class MySensorGPS extends MySensor {

    private static final int INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;

    private FusedLocationProviderClient _fusedLocationProviderClient;
    private LocationCallback _locationCallback;
    private Location _lastLocation;


    /**
     * Initializes the GPS sensor with default values.
     */
    public MySensorGPS() {
        super("GPS", "0", TYPE_NONE);
    }

    /**
     * Activates the GPS sensor and starts receiving location updates.
     *
     * @param activity The activity context used to access the FusedLocationProviderClient.
     */
    public void activateSensor(@NonNull Activity activity) {
//        this._activity = activity;
        this._fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        _locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if(getStatus() != Status.Connected)
                    setStatus(Status.Connected);

                super.onLocationResult(locationResult);
                _lastLocation = locationResult.getLastLocation();

                if(_sensorValueListener != null)
                    _sensorValueListener.onSensorValueChange();
            }
        };

        startLocationUpdates();
    }


    /**
     * Starts location updates with specified interval and priority.
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        System.out.println("Starting location updates");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        _fusedLocationProviderClient.requestLocationUpdates(locationRequest, _locationCallback, Looper.getMainLooper());
    }


    /**
     * Stops receiving location updates.
     */
    public void stopLocationUpdates() {
        _fusedLocationProviderClient.removeLocationUpdates(_locationCallback);
    }


    /**
     * Returns the most recent location received.
     *
     * @return The last known location.
     */
    public Location getLocation() {
        return _lastLocation;
    }


}




