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
     * Called when all data has been sent to the HUD device.
     */
    void onAllDataSent();

    /**
     * Called when there is a request for boot data from the HUD device.
     */
    void onRequestBootData();

    /**
     * Called when the boot process of the HUD device is complete.
     */
    void onBootComplete();

    /**
     * Called when the system is in the process of connecting to the HUD device.
     */
    void onConnecting();
}
