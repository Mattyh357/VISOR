package com.matt.visor.app.hud;

/**
 * Interface for handling Bluetooth events for a HUD device.
 */
public interface HudBluetoothListener {

    /**
     * Called when a connection with the HUD device has been established.
     */
    void onConnected();

    /**
     * Called when the connection with the HUD device has been disconnected.
     */
    void onDisconnected();

    /**
     * Called when the system is in the process of connecting to the HUD device.
     */
    void onConnecting();

    /**
     * Called when data has been successfully sent to the HUD device.
     */
    void onDataSent();

}
