/**
 * This class is part of the V.I.S.O.R app.
 * Manages application-level functionalities including device management, navigation data, and image processing.
 *
 * @version 1.0
 * @since 21/02/2024
 */

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
    private List<Step> _steps;
    private List<LatLng> _polylineRoute;


    /**
     * Loads all saved sensors including gps, hud and additional sensors.
     * This method currently has data hardcoded for current prototype, but these values should
     * be saved in app's config.
     *
     * @param activity
     */
    public void loadDevices(Activity activity) {
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


    /**
     * Saves list of steps and polyline in the application class to ensure it persist when
     * switching activities.
     *
     * @param steps List of steps
     * @param polylineRoute Polyline
     */
    public void saveNavigation(List<Step> steps, List<LatLng> polylineRoute) {
        _steps = steps;
        _polylineRoute = polylineRoute;
    }

    /**
     * Returns bool indicating whether or navigation was saved between activities.
     *
     * @return True if navigation route is saved.
     */
    public boolean isNavigationSaved() {
        return _steps != null && _polylineRoute != null;
    }

    /**
     * Clears saved steps and polyline
     */
    public void unSaveNavigation() {
        _steps = null;
        _polylineRoute = null;
    }

    /**
     * Returns list of steps representing used for navigation.
     *
     * @return List of steps
     */
    public List<Step> getSteps() {
        return _steps;
    }

    /**
     * Returns list of LatLng representing polyline for navigation route.
     *
     * @return Polyline
     */
    public List<LatLng> getPolylineRoute() {
        return _polylineRoute;
    }


    // Images for stuff

    /**
     * Loads and processes custom binary file, creates list of Bitmaps using CustomBinProcessor
     */
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

    /**
     * Returns Bitmap of image corresponding Image ID (position in the list)
     *
     * @param id ID of the image
     * @return Bitmap of the image
     */
    public Bitmap getImageForId(int id) {
        return _listOfImages.get(id);
    }

    /**
     * Retrieves list of Bitmaps representing navigation icons
     *
     * @return List of images
     */
    public List<Bitmap> get_listOfImages() {
        return new ArrayList<>(_listOfImages.values());
    }


}
