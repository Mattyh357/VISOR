/***
 * TODO this class is for TESTING ONLY!!!! it will be completely rewritten for the final version.
 */


package com.matt.visor.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class HUDBLE {


//
//
//
//    @SuppressLint("MissingPermission")
//    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
//        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            System.out.println("PERMISSION");
//            return;
//        }
//        mBluetoothGatt.readCharacteristic(characteristic);
//    }

}