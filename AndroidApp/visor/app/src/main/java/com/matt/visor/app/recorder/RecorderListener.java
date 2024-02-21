/**
 * This class is part of the V.I.S.O.R app.
 * The RecorderListener contains methods which are called from the Recorder class when certain events occur.
 * 
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import com.google.android.gms.maps.model.LatLng;

public interface RecorderListener {

    /**
     * Called when new data is recorded
     * @param speed     Speed (Km/h) of last recording
     * @param distance  Total distance (Km) of last recording
     * @param latLng    Coordinates of the last recording
     */
    void onNewData(double speed, double distance, LatLng latLng);

    /**
     * Called when elapsed time is change
     * @param elapsedTime   Number of second from when the recording was started
     */
    void onEstimatedTimeChange(int elapsedTime);

    /**
     * Called when ride is saved
     */
    void onSavingComplete();

}
