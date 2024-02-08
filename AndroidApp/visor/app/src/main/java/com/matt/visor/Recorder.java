package com.matt.visor;

import java.util.HashMap;
import java.util.Map;

public class Recorder {

    private Thread _thread;
    private boolean _paused = false;
    private final Object _pauseLock = new Object();


    private RecorderListener _recorderListener;


    // TEXT
    private int counter = 0;

    public Recorder() {
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

//                    updateNotification("Counter: " + counter + ", Duration: " + durationSeconds + " sec");
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
        Map<String, Object> data = new HashMap<>();
        data.put("time", counter);

        if(_recorderListener != null) {
            _recorderListener.onNewData(data);
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
