/***
 * TODO this class is for TESTING ONLY!!!! it will be completely rewritten for the final version.
 */


package com.matt.tester_bt_send;

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
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BleHelper {


    private static final String macAddress = "30:83:98:EF:3D:7E";
    private static final UUID serviceUUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID characteristicUUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");


    // TODO move somewhere...
    private static final byte ACK_ERROR = 0x00;
    private static final byte ACK_OK = 0x01;


    public static final byte SPEED_INSTRUCTION = 0x05;
    public static final byte NAVIGATION_IMG_INSTRUCTION = 0x06;


    private static final byte REQUEST_BOOT_DATA = 0x10;
    private static final byte RESTART_BOOTING = 0x11;
    private static final byte IMG_FILE_INSTRUCTION = 0x12;
    private static final byte CONFIG_INSTRUCTION = 0x13;
    private static final byte ALL_BOOT_DATA_SENT = 0x15;
    private static final byte BOOT_DATA_PROCESSED = 0x16;




    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private Activity activity;

    public boolean isConnected;


    // Chunks

    private int _currentChunkIndex = 0;
    private final int CHUNK_SIZE = 20;


    private Timer ackTimeoutTimer;
    private final long ACK_TIMEOUT = 1000;


    private Queue<byte[]> _remainingChunks = new LinkedList<>();


    // Status - crap! should NOT be here (NEVER EVER!) but it's just for testing so.... yeah.. :)
    private TextView _statusText;

    public BleHelper(Activity activity, TextView statusText) {
        this.activity = activity;
        this._statusText = statusText;

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @SuppressLint("MissingPermission")
    public void connect() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        if (device != null) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("PERMISSION");
                return;
            }
            mBluetoothGatt = device.connectGatt(activity, false, mGattCallback);
            _statusText.setText("Connecting");
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("onConnectionStateChange: status: " + status + " - newState: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("PERMISSION");
                    return;
                }

                isConnected = true;
                gatt.discoverServices();
                _statusText.setText("Connected");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false;
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Discover services and find the relevant characteristic
                BluetoothGattService service = gatt.getService(serviceUUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                    if (characteristic != null) {
                        // Enable local notifications
                        gatt.setCharacteristicNotification(characteristic, true);

                        // Enable remote notifications
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] data = characteristic.getValue();
            System.out.println("Received some new data" + data.toString());

            /*
            I can received:

            #define ACK_ERROR 0x00
            #define ACK_OK 0x01


            #define REQUEST_BOOT_DATA 0x10
            #define RESTART_BOOTING 0x11
            #define BOOT_DATA_PROCESSED 0x16

             */

            if(data != null && data.length == 1) {
                byte value = data[0];

                System.out.println("received Value = " + value);

                if(value == ACK_ERROR || value == ACK_OK)
                    onAcknowledgment(value);
                else if (value == REQUEST_BOOT_DATA)
                    startBlastingBootData();
                else if (value == BOOT_DATA_PROCESSED)
                    doSomethingLikeSendNewSpeedOrImage();
                else
                    receivedUnknownInstruction();
            }
            else {
                System.out.println("received crap...so ignoring? probably should do something tbh");
            }


            super.onCharacteristicChanged(gatt, characteristic);
        }
    };


    private void startBlastingBootData() {
        System.out.println("START BLASTING BOOT DATA");
        _statusText.setText("Boot data - requested");
    }

    private void doSomethingLikeSendNewSpeedOrImage() {
        System.out.println("START SENDING NEW SPEED / NAV");
//        _statusText.setText("BootingCompleted - send something");
    }

    private void receivedUnknownInstruction() {
//        System.out.println("receivedUnknownInstruction...");
    }

    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("PERMISSION");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    private void writeData(byte[] data) {
        if (!isConnected || mBluetoothGatt == null) {
            // Not connected or GATT is null
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("PERMISSION");
            return;
        }

        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                characteristic.setValue(data);
                mBluetoothGatt.writeCharacteristic(characteristic);
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void close() {
        if (mBluetoothGatt == null) return;
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("PERMISSION");
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }






    public void sendInstruction(byte instruction) {
        // TODO shouldn't clear here -
        _remainingChunks.clear();

        byte[] data = new byte[3];

        data[0] = instruction;
        data[1] = (byte) (0);
        data[2] = (byte) (0);

        _remainingChunks.add(addChecksum(data));

        sendNextChunk();
    }





    public void sendData(byte instruction, int number) {

        // TODO shouldn't clear here -
        _remainingChunks.clear();


        byte[] data = new byte[3];

        data[0] = instruction;
        data[1] = (byte) ((number >> 8) & 0xff);
        data[2] = (byte) (number & 0xff);

        _remainingChunks.add(addChecksum(data));

        sendNextChunk();
    }

    public void sendBootData(byte[] fileData) {
        System.out.println("sending big data :)");

        // TODO magic
        int headerSize = 3; // 2 bytes for index and 1 byte for checksum
        int dataSize = CHUNK_SIZE - headerSize;
        int totalChunks = (int) Math.ceil((double) fileData.length / dataSize);



        // TODO shouldn't clear here -
        _remainingChunks.clear();



        // Instruction chunk
        byte[] data = new byte[3];
        data[0] = IMG_FILE_INSTRUCTION;
        data[1] = (byte) ((totalChunks >> 8) & 0xff);
        data[2] = (byte) (totalChunks & 0xff);

        _remainingChunks.add(addChecksum(data));


        // add FILE chunks
        addFileChunks(fileData, totalChunks);

        //add config files

        // Add last ALL_BOOT_DATA_SENT instruction
        data[0] = ALL_BOOT_DATA_SENT;
        data[1] = (byte) (0);
        data[2] = (byte) (0);
        _remainingChunks.add(addChecksum(data));


        // All good ;) goooooo
        sendNextChunk();
    }






    private void sendNextChunk() {
        byte[] data = _remainingChunks.peek();

        if(data == null)
            return;

        //debug
        String debugLine = "Sending bytes: ";
        for (byte b : data) {
            debugLine += String.format("%02X ", b);
        }

//        _statusText.setText(debugLine);
        System.out.println(debugLine);



        //send data and start timer
        writeData(data);
        startAckTimeoutTimer();
    }


    private void onAcknowledgment(byte value) {
        cancelAckTimeoutTimer();

        if(value == ACK_OK){
//            _statusText.setText("ACK OK");
            System.out.println("ACK OK! removing first and moving on");
            _remainingChunks.poll(); //is there just remove?
        }
        else {
//            _statusText.setText("ACK ERROR");
            System.out.println("DATA WAS NOT OK!!!! not removing first and sending it again");
        }

        if(_remainingChunks.isEmpty()) {
            // TODO DO SOMETHING :)
            System.out.println("HAVE NO MORE DATA TO SEND");
        } else {
            sendNextChunk();
        }
    }

    private void resendChunk() {
        System.out.println("RESEND - by sending fake error ACK");
        onAcknowledgment(ACK_ERROR);
    }

    private void startAckTimeoutTimer() {
        if (ackTimeoutTimer != null) {
            ackTimeoutTimer.cancel();
        }
        ackTimeoutTimer = new Timer();
        ackTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                resendChunk();
            }
        }, ACK_TIMEOUT);
    }

    private void cancelAckTimeoutTimer() {
        if (ackTimeoutTimer != null) {
            ackTimeoutTimer.cancel();
            ackTimeoutTimer = null;
        }
    }




    public byte[] addChecksum(byte[] data) {
        // Calculate the checksum
        int checksum = 0;
        for (byte b : data) {
            checksum += (b & 0xFF);
        }
        byte checksumByte = (byte) (checksum % 256);

        // Create a new array with extra space for the checksum
        byte[] dataWithChecksum = new byte[data.length + 1];

        // Copy original data into the new array
        System.arraycopy(data, 0, dataWithChecksum, 0, data.length);

        // Add the checksum byte at the end
        dataWithChecksum[dataWithChecksum.length - 1] = checksumByte;

        return dataWithChecksum;
    }



    private void addFileChunks(byte[] data, int totalChunks) {
        System.out.print("TEST ALL  bytes: ");
        for (byte b : data) {
            System.out.printf("%02X ", b);
        }
        System.out.println();


        int headerSize = 3; // 2 bytes for index and 1 byte for checksum
        int dataSize = CHUNK_SIZE - headerSize;

        for (int i = 0; i < totalChunks; i++) {
            int start = i * dataSize;

            if (start >= data.length) {
                break; // Stop if 'start' is out of the data array bounds
            }

            int end = Math.min(start + dataSize, data.length);

            // Create chunk data including the index
            byte[] chunkData = new byte[end - start + 2]; // +2 for the index
            chunkData[0] = (byte) (i & 0xff);          // Lower byte of the index
            chunkData[1] = (byte) ((i >> 8) & 0xff);   // Upper byte of the index

            // Copy actual data into chunkData after index
            System.arraycopy(data, start, chunkData, 2, end - start);


            byte[] withChecksum = addChecksum(chunkData);

            //debug
            System.out.print("ADDING bytes: ");
            for (byte b : withChecksum) {
                System.out.printf("%02X ", b);
            }
            System.out.println();




            // Add checksum and add the chunk to the queue
            _remainingChunks.add(withChecksum);
        }
    }


    // TODO remove - old
    private byte calculateChecksum(byte[] data) {
        int checksum = 0; // Use an int to avoid overflow issues
        System.out.print("Data bytes: ");
        for (byte b : data) {
            System.out.printf("%02X ", b);
            checksum += (b & 0xFF);
        }
        System.out.println(); // New line after printing all bytes
        return (byte) (checksum % 256); // Return the result modulo 256
    }




}
