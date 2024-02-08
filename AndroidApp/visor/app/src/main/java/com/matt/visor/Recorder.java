package com.matt.visor;

import android.location.Location;

import com.matt.visor.app.RecordDataPoint;
import com.matt.visor.app.VisorApplication;

public class Recorder {

    private Thread _thread;
    private boolean _paused = false;
    private final Object _pauseLock = new Object();


    private RecorderListener _recorderListener;


    // TEXT
    private int counter = 0;

    VisorApplication _app;

    public Recorder(VisorApplication app) {

        _app = app;

        _thread = new Thread(() -> {
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

                    // DO SOMETHING
                    System.out.println("blah :)");
                    Thread.sleep(1000);
                    counter++;

                    sendDataToListener("Counter: " + counter);

                    // DO SOMETHING




                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }


    public void setListener(RecorderListener listener) {
        _recorderListener = listener;
    }

    private void sendDataToListener(String test) {

        Location location = _app.deviceManager.getGPS().getLocation();

        if(location == null){
            System.out.println("TODO big problem - location is null");
            return;
        }

        RecordDataPoint recordDataPoint = new RecordDataPoint(location);


        int elapsedTime = counter; // TODO wtf :)

        if(_recorderListener != null) {
            _recorderListener.onNewData(recordDataPoint, elapsedTime);
        }

    }





    public void start() {
        _thread.start();
    }

    public void pause() {
        synchronized (_pauseLock) {
            _paused = true;
        }
    }

    public void resume() {
        synchronized (_pauseLock) {
            _paused = false;
            _pauseLock.notifyAll();
        }
    }

    public void stop() {
        _thread.interrupt();
    }


    public boolean isPaused() {
        return _paused;
    }

    public int getTime() {
        return counter;
    }
}
