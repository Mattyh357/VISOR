package com.matt.visor.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MySensorGPS extends MySensor {

    private FusedLocationProviderClient _fusedLocationProviderClient;
    private LocationCallback _locationCallback;
    private Location _lastLocation;

    public MySensorGPS() {
        super("GPS", "0", TYPE_NONE);
    }

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

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        System.out.println("Starting location updates");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Adjust as necessary
        locationRequest.setFastestInterval(3000); // Adjust as necessary
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        _fusedLocationProviderClient.requestLocationUpdates(locationRequest, _locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        _fusedLocationProviderClient.removeLocationUpdates(_locationCallback);
    }

    public Location getLocation() {
        return _lastLocation;
    }


}




