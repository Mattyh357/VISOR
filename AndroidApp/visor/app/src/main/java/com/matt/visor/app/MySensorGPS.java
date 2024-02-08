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

import java.util.HashMap;
import java.util.Map;

public class MySensorGPS extends MySensor {

    private Activity _activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location lastLocation;

    public MySensorGPS(@NonNull Activity activity) {
        super("map", "0", TYPE_NONE);
        isMap = true;
    }

    public MySensorGPS() {
        super("map", "0", TYPE_NONE);
        isMap = true;

    }

    public void activateSensor(@NonNull Activity activity) {
        this._activity = activity;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(getStatus() != Status.Connected)
                    setStatus(Status.Connected);

                super.onLocationResult(locationResult);
                lastLocation = locationResult.getLastLocation();
                System.out.println("Location update: " + lastLocation);

                if(_sensorValueListener != null)
                    _sensorValueListener.onSensorValueChange();
            }
        };

        startLocationUpdates();
    }

    @Override
    public Map<String, Object> getValues() {
        Map<String, Object> values = new HashMap<>();

        if(lastLocation != null) {
            values.put("latitude", lastLocation.getLatitude());
            values.put("longitude", lastLocation.getLongitude());
            values.put("altitude", lastLocation.hasAltitude() ? lastLocation.getAltitude() : -1);
            values.put("speed", lastLocation.hasSpeed() ? lastLocation.getSpeed() : -1);
            values.put("timeInSeconds", lastLocation.getTime());
        }

        return values;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        System.out.println("Starting location updates");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Adjust as necessary
        locationRequest.setFastestInterval(3000); // Adjust as necessary
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public Location getLocation() {
        return lastLocation;
    }


}




