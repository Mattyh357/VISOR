package com.matt.visor.app;


import android.app.Activity;
import android.app.Application;

public class VisorApplication extends Application {

    public static final String JOURNEY_FOLDER = "journeys";
    public DeviceManager deviceManager = new DeviceManager();

//    public List<Journey> _listOfJourneys;


    public boolean navigationEnabled = false;

    public void loadDevices(Activity activity) {

        //hud
        deviceManager.setHUD(new HudUnit(activity , "","",""));

        //gps
//        deviceManager.addSensor(new MySensorGPS());
//        deviceManager.addSensor(new MySensor("FAKE GPS", null, MySensor.TYPE_GPS));

        deviceManager.setGPS(new MySensorGPS());

        //devices
        deviceManager.addSensor(new MySensor("Heart rate", null, MySensor.TYPE_HR));
        deviceManager.addSensor(new MySensor("Cadence", "", MySensor.TYPE_CAD));
        deviceManager.addSensor(new MySensor("Power1234", "", MySensor.TYPE_PWR));

    }

//    public void loadListOfJourneys(Context context) {
//        JourneyLoader jl = new JourneyLoader(context);
//        _listOfJourneys = jl.getListOfJourneys();
//    }
//
//
//    public List<Journey> getListOfJourneys() {
//        return _listOfJourneys;
//    }



}
