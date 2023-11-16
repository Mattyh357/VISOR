//
// Created by matt on 26/10/23.
//


//https://github.com/nkolban/esp32-snippets/tree/master

#pragma once

#include <BLEServer.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLE2902.h>

//#include "/home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/libraries/BLE/src/BLEDevice.h"

class Bluetooth {
public:
    Bluetooth() {};
    Bluetooth(const char* deviceName, const char* serviceID, const char* charID);

    void begin(Bluetooth* instance);
    bool isConnected();
    void setAdvertising(bool status);

    void sendData(uint8_t data);
    bool isThereNewData();
    std::string getData();

//    void getOOB();
//    void clearLTK()


    //TESTING
    void onReceivedData(const uint8_t* data, size_t length);

private:
    //TODO comments
    void wipeData();


    /** @brief The device name that is displayed during discovery. */
    const char* _deviceName;

    /** @brief The BLE server instance for managing BLE connections and services. */
    BLEServer* _pServer;

    /** @brief Represents a BLE characteristic which is a data container on the BLE server. */
    BLECharacteristic* _pCharacteristic;

    /** @brief Universally Unique Identifier for the BLE service. */
    const char* _serviceUUID;

    /** @brief Universally Unique Identifier for the BLE characteristic. */
    const char* _charUUID;

    /** @brief TODO Comment :) */
    bool _newDataAvailable;


    //TESTING
    static uint8_t calculateChecksum(const uint8_t* data, size_t length);
    void sendAcknowledgment(bool isSuccessful);



};




// TODO power management? Put into low power maybe?
// TODO when receive data, check acknowledgement is required and if so, send it
// TODO over-the-air updates?
