/**
 * This class is part of the V.I.S.O.R app.
 * The waypoint class serves as a data struct to hold data about one individual point of recorded journey.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import android.location.Location;

public class Waypoint {

    private double _latitude;
    private double _longitude;
    private double _altitude;
    private double _speed;
    private Long _time;


    /**
     * Default constructor initializing all values to zero for latitude, longitude, altitude, speed, and time.
     */
    public Waypoint() {
        _latitude = 0;
        _longitude = 0;
        _altitude = 0;
        _speed = 0;
        _time = 0L;
    }

    /**
     * Initializes waypoint with location data, converting units to degrees, kilometers, km/h, and seconds.
     *
     * @param location The location object to initialize from.
     */
    public Waypoint(Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
        _altitude = location.getAltitude() / 1000; // m -> km
        _speed = location.getSpeed() * 3.6; // m/s -> km/s
        _time = location.getTime() / 1000; // ms -> s
    }

    /**
     * Returns the latitude in degrees.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return _latitude;
    }

    /**
     * Returns the longitude in degrees.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return _longitude;
    }

    /**
     * Returns the altitude in kilometers.
     *
     * @return The altitude.
     */
    public double getAltitude() {
        return _altitude;
    }

    /**
     * Returns the speed in kilometers per hour.
     *
     * @return The speed.
     */
    public double getSpeed() {
        return _speed;
    }

    /**
     * Returns the time in seconds since epoch.
     *
     * @return The time.
     */
    public Long getTime() {
        return _time;
    }

    /**
     * Sets the latitude in degrees.
     *
     * @param latitude The latitude to set.
     */
    public void setLatitude(double latitude) {
        _latitude = latitude;
    }

    /**
     * Sets the longitude in degrees.
     *
     * @param longitude The longitude to set.
     */
    public void setLongitude(double longitude) {
        _longitude = longitude;
    }

    /**
     * Sets the altitude in kilometers.
     *
     * @param altitude The altitude to set.
     */
    public void setAltitude(double altitude) {
        _altitude = altitude;
    }

    /**
     * Sets the speed in kilometers per hour.
     *
     * @param speed The speed to set.
     */
    public void setSpeed(double speed) {
        _speed = speed;
    }

    /**
     * Sets the time in seconds since epoch.
     *
     * @param time The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }
}
