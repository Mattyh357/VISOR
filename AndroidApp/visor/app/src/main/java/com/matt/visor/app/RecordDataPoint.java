package com.matt.visor.app;

import android.location.Location;

public class RecordDataPoint {

    private double _latitude;
    private double _longitude;
    private double _altitude;

    private float _speed;
    private Long _time;


    public RecordDataPoint(Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
        _altitude = location.getAltitude();
        _speed = location.getSpeed();
        _time = location.getTime();
    }


    public double get_latitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }

    public double getAltitude() {
        return _altitude;
    }

    public float getSpeed() {
        return _speed;
    }


    public Long getTime() {
        return _time;
    }
}
