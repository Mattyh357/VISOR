/**
 * This class is part of the V.I.S.O.R app.
 * The Recorder class is responsible for running a separate thread which check the GPS location in
 * set interval and record a data (gps data + additional sensors).
 * Utilizing pauseLock, this thread can be paused and resumed.
 * Lastly, it can call the JourneyLoaderAndSaver to save the recorded route.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.matt.visor.app.VisorApplication;

public class Recorder {

    private final Thread _thread;
    private boolean _paused = false;
    private final Object _pauseLock = new Object();
    private RecorderListener _recorderListener;
    private int elapsedSeconds = 0;
    private final VisorApplication _app;
    private final Journey _journey;



    public Recorder(VisorApplication app) {
        _app = app;
        _journey = new Journey();
        _thread = getRecorderThread();
    }


    /**
     * Defines the thread which is to run method "recordData" every 1s unless its paused.
     *
     * @return Thread
     */
    private Thread getRecorderThread() {
        return new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (_pauseLock) {
                    while (_paused) {
                        try {
                            _pauseLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                try {
                    recordData();
                    Thread.sleep(1000);
                    elapsedSeconds++;

                    if(_recorderListener != null) {
                        _recorderListener.onEstimatedTimeChange(elapsedSeconds);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }


    /**
     * Record data using GPS sensor as well as all additional sensors that are connected
     * If the location is same as it was previous reading, will do nothing.
     */
    private void recordData() {

        //Get data from GPS
        Location location = _app.deviceManager.getGPS().getLocation();

        /// If it's different then save it
        // No reason to save -> exit
        if(location == null || location.getTime() == _journey.getLastWaypoint().getTime())
            return;

        // Filter 0x0 location
        if(location.getLatitude() == 0 && location.getLongitude() == 0)
            return;

        // Save location
        Waypoint waypoint = new Waypoint(location);

        // TODO Additional data to be saved

        _journey.addWaypoint(waypoint);

        // Cos it was saved -> inform the listener
        if(_recorderListener != null) {
            LatLng latLng = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
            _recorderListener.onNewData(location.getSpeed(), _journey.getTotalDistance(), latLng);
        }
    }

    /**
     * Sets Listener for notifications when new data have been recorded, and when ride is saved
     *
     * @param listener Listener
     */
    public void setListener(RecorderListener listener) {
        _recorderListener = listener;
    }


    /**
     * Starts recording
     */
    public void start() {
        _thread.start();
    }


    /**
     * Pauses recording
     */
    public void pause() {
        synchronized (_pauseLock) {
            _paused = true;
        }
    }

    /**
     * Resumes recording
     */
    public void resume() {
        synchronized (_pauseLock) {
            _paused = false;
            _pauseLock.notifyAll();
        }
    }

    /**
     * Stops the recorder permanently
     */
    public void stop() {
        _thread.interrupt();
    }

    /** Is the recorder paused
     *
     * @return return boolean indicating if the recorder is paused
     */
    public boolean isPaused() {
        return _paused;
    }

    /** Saves all recorded data using JourneyLoaderAndSaver and notify the listener when done
     *
     * @param context Context of the application
     * @param map Reference to the map use to capture a thumbnail for the ride
     */
    public void saveData(Context context, GoogleMap map) {
        JourneyLoaderAndSaver.saveJourney(context, map, _journey, new JourneyLoaderAndSaver.Callback() {
            @Override
            public void onSaveComplete(String journeyID) {
                if(_recorderListener != null)
                    _recorderListener.onSavingComplete(journeyID);
            }
        });
    }
}
