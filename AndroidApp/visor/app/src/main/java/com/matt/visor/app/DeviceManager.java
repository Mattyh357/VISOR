/**
 * This class is part of the V.I.S.O.R app.
 * Serves as a container for all connected sensors including HUD and GPS.
 *
 * @version 1.0
 * @since 08/02/2024
 */


package com.matt.visor.app;


import android.app.Activity;

import com.matt.visor.app.hud.HudUnit;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager {

    private final List<MySensor> _allSensors = new ArrayList<>();
    private HudUnit _hud;
    private MySensorGPS _gps;

    /**
     * Initializes the device manager and its components. (tbd)
     */
    public DeviceManager() {
        System.out.println("Device MANAGER INITIALIZED");
    }

    /**
     * Activates all sensors including GPS and HUD for a given activity.
     *
     * @param activity The activity context for sensor activation.
     */
    public void activateAll(Activity activity) {
        _hud.connect();
        _gps.activateSensor(activity);

        for (MySensor sensor : _allSensors) {
            sensor.activate();
        }
    }

    /**
     * Sets the HUD unit for this device manager.
     *
     * @param device The HUD unit to be set.
     */
    public void setHUD(HudUnit device) {
        _hud = device;
    }

    /**
     * Retrieves the currently set HUD unit.
     *
     * @return The current HUD unit.
     */
    public HudUnit getHUD() {
        return _hud;
    }

    /**
     * Sets the GPS sensor for this device manager.
     *
     * @param sensorGPS The GPS sensor to be set.
     */
    public void setGPS(MySensorGPS sensorGPS) {
        _gps = sensorGPS;
    }

    /**
     * Retrieves the currently set GPS sensor.
     *
     * @return The current GPS sensor.
     */
    public MySensorGPS getGPS() {
        return _gps;
    }

    /**
     * Adds a new sensor to the device manager.
     *
     * @param sensor The sensor to add.
     */
    public void addSensor(MySensor sensor) {
        _allSensors.add(sensor);
    }

    /**
     * Retrieves a sensor by its position in the list.
     *
     * @param pos The position of the sensor in the list.
     * @return The sensor at the specified position.
     */
    public MySensor getSensor(int pos) {
        return _allSensors.get(pos);
    }

    /**
     * Retrieves all sensors managed by the device manager.
     *
     * @return A list of all sensors.
     */
    public List<MySensor> getAllSensors() {
        return _allSensors;
    }

}
