package com.matt.visor.app;


import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.matt.visor.GoogleMap.Step;
import com.matt.visor.app.hud.HudUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisorApplication extends Application {


    public DeviceManager deviceManager = new DeviceManager();
    private Map<Integer, Bitmap> _listOfImages;

    public boolean navigationEnabled = false;


    public void loadDevices(Activity activity) {
        // TODO probably shouldn't be hardcoded :)
        String macAddress = "30:83:98:FB:D6:7A";
        String sUUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
        String cUUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";

        deviceManager.setHUD(new HudUnit(activity, macAddress, sUUID, cUUID));
        deviceManager.setGPS(new MySensorGPS());

        //devices
        deviceManager.addSensor(new MySensor("Heart rate", null, MySensor.TYPE_HR));
        deviceManager.addSensor(new MySensor("Cadence", "", MySensor.TYPE_CAD));
        deviceManager.addSensor(new MySensor("Power1234", "", MySensor.TYPE_PWR));
    }



    private List<Step> _steps;
    private List<LatLng> _polylineRoute;

    public void saveNavigation(List<Step> steps, List<LatLng> polylineRoute) {
        _steps = steps;
        _polylineRoute = polylineRoute;
    }

    public boolean isNavigationSaved() {
        return _steps != null && _polylineRoute != null;
    }

    public void unSaveNavigation() {
        _steps = null;
        _polylineRoute = null;
    }

    public List<Step> getSteps() {
        return _steps;
    }

    public List<LatLng> getPolylineRoute() {
        return _polylineRoute;
    }












    // Images for stuff




    public void LoadImages() {

        File file = new File(this.getFilesDir(), "images.cbi");
        CustomBinProcessor cbp = new CustomBinProcessor(file);

        _listOfImages = new HashMap<>();
        int i = 0;

        for (Bitmap bitmap : cbp.getListOfBitmaps()) {
            _listOfImages.put(i, bitmap);
            i++;
        }

        System.out.println("done");

    }

    public Bitmap getImageForId(int id) {
        return _listOfImages.get(id);
    }

    public List<Bitmap> get_listOfImages() {
        return new ArrayList<>(_listOfImages.values());
    }




}
