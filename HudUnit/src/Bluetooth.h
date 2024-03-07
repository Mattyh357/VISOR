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


#define ACK_ERROR 0x00
#define ACK_OK 0x01
#define SPEED_INSTRUCTION 0x05
#define NAVIGATION_IMG_INSTRUCTION 0x06
#define REQUEST_BOOT_DATA 0x10
#define RESTART_BOOTING 0x11
#define IMG_FILE_INSTRUCTION 0x12
#define CONFIG_INSTRUCTION 0x13
#define ALL_BOOT_DATA_SENT 0x15
#define BOOT_DATA_PROCESSED 0x16


#define CHECKSUM_SIZE 1

class Bluetooth {
public:
    Bluetooth() {};
    Bluetooth(const char* deviceName, const char* serviceID, const char* charID);

    void begin(Bluetooth* instance);
    bool isConnected();
    void setAdvertising(bool status);

    void sendData(uint8_t data);
    bool isThereNewData();

    //TESTING
    void onReceivedData(const uint8_t* data, size_t length);
    std::vector<uint8_t> _fileStorage;
    uint8_t last_instruction;
    uint16_t last_data;
    uint16_t getData();

    uint16_t _remainingChunks;
    uint16_t _totalChunks;



private:

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


    //TESTING some methods
    static uint8_t calculateChecksum(const uint8_t* data, size_t length);
    void sendAcknowledgment(bool isSuccessful);
    void handleFileChunk(const uint8_t* chunkData, size_t length);
    void onCompleteReceivedData(uint8_t instruction, uint16_t data);


};




// TODO power management? Put into low power maybe?
// TODO when receive data, check acknowledgement is required and if so, send it
// TODO over-the-air updates?
