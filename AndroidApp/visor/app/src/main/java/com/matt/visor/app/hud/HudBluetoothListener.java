package com.matt.visor.app.hud;

public interface HudBluetoothListener {
    void onConnected();
    void onDisconnected();
    void onAllDataSent();
    void onRequestBootData();
    void onBootComplete();

    void onConnecting();
}