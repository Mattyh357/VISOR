package com.matt.visor.app;


import com.matt.visor.app.hud.HudUnit;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager {

    private final List<MySensor> _allSensors = new ArrayList<>();
    private HudUnit _hud;
    private MySensorGPS _gps;

    public DeviceManager() {
        System.out.println("Device MANAGER INITIALIZE");
    }

    public void addSensor(MySensor sensor) {
        _allSensors.add(sensor);
        //TODO order by type!!!
    }

    public MySensor getSensor(int pos) {
        return _allSensors.get(pos);
    }

    public List<MySensor> getAllSensors() {
        return _allSensors;
    }


    public void setHUD(HudUnit device) {
        _hud = device;
    }

    public void setGPS(MySensorGPS sensorGPS) {
        _gps = sensorGPS;
    }

    public MySensorGPS getGPS() {
        return _gps;
    }

    public HudUnit getHUD() {
        return _hud;
    }

//    public void forgetSensor(int type) {
//        _allSensors.get(type).setAddress(null);
//        _allSensors.get(type).setStatus(MySensor.Status.NotFound);
//    }
}
