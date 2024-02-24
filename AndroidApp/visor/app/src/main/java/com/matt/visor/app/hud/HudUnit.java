// TODO Comments this

package com.matt.visor.app.hud;

import android.app.Activity;

import com.matt.visor.app.SensorStatusListener;

public class HudUnit implements HudBluetoothListener{

    private final Activity _activity;
    private final HudBluetoothManager _bt;
    protected SensorStatusListener _sensorStatusListener;
    private Status _status;

    public enum Status {
        Disconnected,
        Connecting,
        Connected,
        Boot,
        NotAssigned,
    }


    /**
     * Initializes the HUD unit with Bluetooth settings and starts in a disconnected state.
     *
     * @param activity The activity context.
     * @param macAddress The MAC address of the Bluetooth device.
     * @param sUUID The service UUID for the Bluetooth connection.
     * @param cUUID The characteristic UUID for the Bluetooth connection.
     */
    public HudUnit(Activity activity, String macAddress, String sUUID, String cUUID) {
        _bt = new HudBluetoothManager(activity, this, macAddress, sUUID, cUUID);

        // TODO boot file
        _status = Status.Disconnected;
        _activity = activity;
    }

    /**
     * Sets a listener to receive status updates of the sensor.
     *
     * @param listener The listener to set for status updates.
     */
    public void setStatusChangeListener(SensorStatusListener listener) {
        this._sensorStatusListener = listener;
    }

    /**
     * Initiates a connection to the HUD via Bluetooth.
     */
    public void connect() {
        _bt.connect(_activity);
    }

    /**
     * Returns a string representation of the current connection status.
     *
     * @return The current status as a string.
     */
    public String getStatusString() {
        switch (_status) {
            case Disconnected:
                return "Disconnected";
            case Connecting:
                return "Connecting...";
            case Connected:
                return "Connected";
            case Boot:
                return "Booting...";
            case NotAssigned:
                return "Not Assigned";
        }

        return "Unknown";
    }

    /**
     * Sends a speed value to the HUD device.
     *
     * @param speed The speed value to send.
     */
    public void sendSpeed(int speed) {
        _bt.sendData(_bt.SPEED_INSTRUCTION, speed);
    }

    /**
     * Sends an image identifier to the HUD device for display.
     *
     * @param image The image identifier to send.
     */
    public void sendImg(int image) {
        _bt.sendData(_bt.NAVIGATION_IMG_INSTRUCTION, image);
    }

    /**
     * Updates the internal status of the HUD unit and notifies the status change listener.
     *
     * @param status The new status to set.
     */
    private void setStatus(Status status) {
        _status = status;
        if(_sensorStatusListener != null)
            _sensorStatusListener.onChange();
    }


    /**
     * HudBluetoothListener interface implementations
     */

    @Override
    public void onConnecting() {
        setStatus(Status.Connecting);
    }

    @Override
    public void onConnected() {
        setStatus(Status.Connected);
    }

    @Override
    public void onDisconnected() {
        setStatus(Status.Disconnected);
    }

    @Override
    public void onAllDataSent() {   }

    @Override
    public void onRequestBootData() {
        setStatus(Status.Boot);
    }

    @Override
    public void onBootComplete() {
        setStatus(Status.Connected);
    }


}
