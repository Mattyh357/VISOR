/**
 * This class is part of the V.I.S.O.R app.
 * The HudBluetoothManager is responsible for managing Bluetooth communication for Hud unit, handling
 * connection, data transmission, and reception utilizing HudBluetoothListener for callbacks.
 * Upon connection to the device, it tries to negotiate higher MTU size so that data wouldn't have to
 * be sent using default 20Bytes/chunk.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.hud;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class HudBluetoothManager {
    private final Activity _activity;

    // Bluetooth
    private static String _macAddress;
    private static UUID _serviceUUID;
    private static UUID _characteristicUUID;
    private final BluetoothManager _bluetoothManager;
    private final BluetoothAdapter _bluetoothAdapter;
    private BluetoothGatt _bluetoothGatt;

    // Ready
    private boolean _connected = false;
    private boolean _booted = false;


    // Chunks
    private final int DEFAULT_CHUNK_SIZE = 20;
    private final int OPTIMAL_CHUNK_SIZE = 100; // TODO better number
    private int _chunkSize = DEFAULT_CHUNK_SIZE;
    private Queue<byte[]> _remainingChunks = new LinkedList<>();

    // Listener
    private final HudBluetoothListener _listener;


    // Timers
    private Timer ackTimeoutTimer;
    private final long ACK_TIMEOUT = 1000;





    /**
     * Initializes Bluetooth connection parameters.
     *
     * @param activity The associated activity.
     * @param listener Callback for Bluetooth events.
     * @param macAddress The MAC address of the Bluetooth device.
     * @param sUUID The service UUID.
     * @param cUUID The characteristic UUID.
     */
    public HudBluetoothManager(Activity activity, HudBluetoothListener listener, String macAddress, String sUUID, String cUUID) {
        _macAddress = macAddress;
        _serviceUUID = UUID.fromString(sUUID);
        _characteristicUUID = UUID.fromString(cUUID);
        _activity = activity;
        _listener = listener;

        // Init bluetooth
        _bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        _bluetoothAdapter = _bluetoothManager.getAdapter();
    }

    /**
     * Attempts to establish a Bluetooth connection with a device.
     *
     * @param activity The context for the connection attempt.
     */
    @SuppressLint("MissingPermission")
    //TODO permissions
    public void connect(Activity activity) {
        System.out.println("Trying to connect");
        BluetoothDevice device = _bluetoothAdapter.getRemoteDevice(_macAddress);
        if (device != null) {
            System.out.println("here");
            _bluetoothGatt = device.connectGatt(activity, true, gattCallback);

            Optional.ofNullable(_listener).ifPresent(HudBluetoothListener::onConnecting);
        }
    }

    /**
     * Disconnects from the Bluetooth device and updates connection status.
     */
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (_bluetoothGatt == null) return;
        _bluetoothGatt.close();
        _bluetoothGatt = null;
        if(_listener != null )
            _listener.onConnected();

        Optional.ofNullable(_listener).ifPresent(HudBluetoothListener::onDisconnected);
        _connected = false;
    }

    /**
     * Sends data to the connected Bluetooth device.
     *
     * @param instruction The instruction byte to send.
     * @param number The integer data to send, split into bytes.
     */
    public void sendData(byte instruction, int number) {
        if(!_connected || !_booted)
            return;

        _remainingChunks.clear();

        byte[] data = new byte[3];
        data[0] = instruction;
        data[1] = (byte) ((number >> 8) & 0xff);
        data[2] = (byte) (number & 0xff);

        _remainingChunks.add(addChecksum(data));
        sendNextChunk();
    }



    // TODO fix this whole thing....
    public void sendBootData() {

        if(!_connected)
            return;

        //TODO CRAP here

        File file = new File(_activity.getFilesDir(), "images.cbi");

        byte[] data1 = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(data1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //TODO CRAP here

        byte[] fileData = data1;

        System.out.println("sending big data :)");

        // TODO magic
        int headerSize = 3; // 2 bytes for index and 1 byte for checksum
        int dataSize = _chunkSize - headerSize;
        int totalChunks = (int) Math.ceil((double) fileData.length / dataSize);

        // TODO shouldn't clear here -
        _remainingChunks.clear();

        // Instruction chunk
        byte[] data = new byte[3];
        data[0] = InstructionsByte.IMG_FILE_INSTRUCTION;
        data[1] = (byte) ((totalChunks >> 8) & 0xff);
        data[2] = (byte) (totalChunks & 0xff);

        _remainingChunks.add(addChecksum(data));

        // add FILE chunks
        addFileChunks(fileData, totalChunks);

        //add config files

        // Add last ALL_BOOT_DATA_SENT instruction
        data[0] = InstructionsByte.ALL_BOOT_DATA_SENT;
        data[1] = (byte) (0);
        data[2] = (byte) (0);
        _remainingChunks.add(addChecksum(data));

        // All good ;)
        sendNextChunk();
    }

    /**
     * Starts a timer to handle acknowledgment timeout for chunk resending.
     */
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

    /**
     * Cancels the acknowledgment timeout timer if it's running.
     */
    private void cancelAckTimeoutTimer() {
        if (ackTimeoutTimer != null) {
            ackTimeoutTimer.cancel();
            ackTimeoutTimer = null;
        }
    }

    /**
     * Resends the current chunk by simulating an error acknowledgment.
     */
    private void resendChunk() {
        System.out.println("RESEND - by sending fake error ACK");
        onReceivedData(InstructionsByte.ACK_ERROR);
    }

    /**
     * Adds a checksum byte to the data array for error checking.
     *
     * @param data The original data array without checksum.
     * @return A new data array with the checksum byte appended.
     */
    private byte[] addChecksum(byte[] data) {
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

    /**
     * Splits data into chunks, adds indices, checksums, and queues them for sending.
     *
     * @param data The data to be split into chunks.
     * @param totalChunks The total number of chunks to create.
     */
    private void addFileChunks(byte[] data, int totalChunks) {
        int headerSize = 3; // 2 bytes for index and 1 byte for checksum
        int dataSize = _chunkSize - headerSize;

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

            // Add checksum and add the chunk to the queue
            _remainingChunks.add(withChecksum);
        }
    }

    /**
     * Writes data to the Bluetooth device if connected and service is available.
     *
     * @param data The byte array data to write to the device.
     */
    @SuppressLint("MissingPermission")
    private void writeData(byte[] data) {
        // Stop if there is no gatt
        if(_bluetoothGatt == null){
            System.out.println("Write data - GATT is null");
            return;
        }

        // Stop if not connected
        if (!_connected) {
            System.out.println("Not connected");
            return;
        }

        // Write
        BluetoothGattService service = _bluetoothGatt.getService(_serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(_characteristicUUID);
            if (characteristic != null) {
                characteristic.setValue(data);
                _bluetoothGatt.writeCharacteristic(characteristic);
            }
        }
    }

    /**
     * Sends the next chunk from the queue and starts the acknowledgment timeout timer.
     */
    private void sendNextChunk() {
        // Remove last one
        byte[] data = _remainingChunks.peek();

        if(data == null)
            return;

        //send data and start timer
        writeData(data);
        startAckTimeoutTimer();
    }





    /**
     * Handles received data instructions from the Bluetooth device.
     * Processes various instructions like ACK_OK, ACK_ERROR, and boot data requests.
     * ACK_OK: Confirms successful data reception, moves to next chunk, or ends transmission.
     * ACK_ERROR: Indicates error, attempts to resend the current chunk.
     * REQUEST_BOOT_DATA: Initiates sending of boot data sequence.
     * RESTART_BOOTING and BOOT_DATA_PROCESSED: Manage booting process states.
     *
     * @param value The byte value of the received instruction.
     */
    private void onReceivedData(byte value) {

        // Can received: ACK_OK, ACK_ERROR. REQUEST_BOOT_DATA, RESTART_BOOTING, BOOT_DATA_PROCESSED

        if(value == InstructionsByte.ACK_OK) {
            System.out.println("ACK OK! removing first chunk and moving on");
            _remainingChunks.poll();

            if(_remainingChunks.isEmpty()) {
                // TODO DO SOMETHING :)
                System.out.println("HAVE NO MORE DATA TO SEND");
                cancelAckTimeoutTimer();
                Optional.ofNullable(_listener).ifPresent(HudBluetoothListener::onAllDataSent);
            } else {
                sendNextChunk();
            }
        }
        else if(value == InstructionsByte.ACK_ERROR) {
            System.out.println("DATA WAS NOT OK!!!! not removing first and sending it again");
            sendNextChunk();
        }
        else if (value == InstructionsByte.REQUEST_BOOT_DATA){
            System.out.println("START BLASTING BOOT DATA - TODO automate");
            Optional.ofNullable(_listener).ifPresent(HudBluetoothListener::onRequestBootData);
            sendBootData();
            _booted = false;
        }
        else if (value == InstructionsByte.BOOT_DATA_PROCESSED){
            System.out.println("BOOTING Complete");
            Optional.ofNullable(_listener).ifPresent(HudBluetoothListener::onBootComplete);
            _booted = true;
        }
        else{
            System.out.println("receivedUnknownInstruction...");
        }
    }










    /**
     * Callback for changes in BluetoothGatt state, such as connection changes, service discoveries, MTU changes, and characteristic changes.
     * Handles connection and disconnection events, discovers services and characteristics upon connection,
     * enables notifications for characteristic changes, and adjusts MTU size for optimal data chunking.
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        /**
         * Handles changes in connection state, triggers service discovery on connect.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connected = false;
            }
        }

        /**
         * Processes discovered services and characteristics, enables notifications.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(_serviceUUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(_characteristicUUID);
                    if (characteristic != null) {
                        // Enable local notifications
                        gatt.setCharacteristicNotification(characteristic, true);

                        // Enable remote notifications
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); // TODO magic
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }

                        // Request new bigger MTU
                        gatt.requestMtu(200);
                    }
                }
            }
        }

        /**
         * Adjusts the MTU size upon change to optimize data transmission.
         */
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                _chunkSize = OPTIMAL_CHUNK_SIZE;
                System.out.println("MTU CHANGED: " + _chunkSize);
            }
        }

        /**
         * Responds to characteristic changes, processing received data.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            System.out.println("onCharacteristicChanged");
            byte[] data = characteristic.getValue();

            if(data != null && data.length == 1) {
                byte value = data[0];
                System.out.println("received Value = " + value);
                onReceivedData(value);
            }
            else {
                System.out.println("received crap...so ignoring? probably should do something tbh");
            }

            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

}



