/**
 *  @file Bluetooth.h
 *  @brief Header file for class: Bluetooth
 *
 *  The Bluetooth class provides simplified interface for initializing and handling both incoming and outgoing
 *  BLE communication. It uses the ESP32 BLE library, specifically BLEServer, BLEDevice, BLEUtils, and BLE2902 classes.
 *
 *  This class was written based on examples provided with ESP32 BLE library found here:
 *  https://github.com/nkolban/esp32-snippets/tree/master
 *
 *  @todo over-the-air updates
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */


#pragma once

#include <BLEServer.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <Arduino.h>
#include <sstream>
#include <vector>
#include <string>
#include "Utils.h"

/** @brief Byte for error acknowledgment. */
#define ACK_ERROR 0x00

/** @brief Byte for ok acknowledgment. */
#define ACK_OK 0x01

/** @brief Instruction number identifying that received data are speed related. */
#define INSTRUCTION_SPEED_TEST 0x05

/** @brief Instruction number identifying that received data are navigation. */
#define INSTRUCTION_NAVIG_TEST 0x06

/** @brief TODO not sure yet. */
#define CHECKSUM_SIZE 1


class Bluetooth {
public:

    /**
     * @brief Default constructor for the Bluetooth class.
     */
    Bluetooth() {};

    /**
     * @brief Constructor for the Bluetooth class.
     *
     * Initializes the Bluetooth Low Energy (BLE) device, sets up the server, and registers server callbacks.
     * This setup includes initializing the BLE device with a given name, creating a BLE server, and setting
     * the server callbacks to handle client connections and disconnections.
     *
     * @param deviceName The name of the BLE device.
     * @param serviceID The UUID of the BLE service.
     * @param charID The UUID of the BLE characteristic.
     */
    Bluetooth(const char* deviceName, const char* serviceID, const char* charID);

    /**
     * @brief Starts the BLE service and advertising.
     *
     * Creates a BLE service and characteristic based on the provided UUIDs during the object's initialization.
     * Sets characteristic properties and descriptors, starts the service, and begins advertising the service
     * so it can be discovered by clients.
     *
     * @param instance A pointer to the Bluetooth class instance, used for callbacks.
     */
    void begin(Bluetooth* instance);

    /**
     * @brief Checks if any client is currently connected to the BLE server.
     *
     * Returns true if there is at least one client connected to the server, otherwise returns false.
     *
     * @return A boolean indicating if any client is connected.
     */
    bool isConnected();

    /**
     * @brief Callback function triggered when new data is received from a client.
     *
     * Stores the received data for later processing and sets a flag indicating that new data is available.
     *
     * @param data A vector of strings representing the received data.
     */
    void onReceivedData(std::vector<std::string>);

    /**
     * @brief Sends an acknowledgment to connected clients.
     *
     * Sends a predefined acknowledgment byte to all connected clients, indicating whether a previously
     * received command was successfully processed.
     *
     * @param isSuccessful A boolean indicating the success of the previous operation.
     */
    void sendAcknowledgment(bool isSuccessful);

    /**
     * @brief Checks if there is new data available that was received from a client.
     *
     * Returns true if new data has been received from a client, otherwise returns false. This is controlled
     * by a flag that is set when new data is received.
     *
     * @return A boolean indicating if new data is available.
     */
    bool isThereNewData();

    /**
     * @brief Retrieves the most recently received data and resets the new data available flag.
     *
     * Returns the data received from clients and resets the flag indicating that new data is available,
     * to signal that the data has been read.
     *
     * @return A vector of strings representing the most recently received data.
     */
    std::vector<std::string> getData();


private:

    /**
     * @brief Sends a single byte of data to all connected clients.
     *
     * If there is at least one connected client, it sets the characteristic's value to the provided data byte
     * and notifies all connected clients about the update.
     *
     * @param data The single byte of data to send to connected clients.
     */
    void sendData(uint8_t data);


    /** @brief The BLE server instance for managing BLE connections and services. */
    BLEServer* _pServer;

    /** @brief Represents a BLE characteristic which is a data container on the BLE server. */
    BLECharacteristic* _pCharacteristic;

    /** @brief Universally Unique Identifier for the BLE service. */
    const char* _serviceUUID;

    /** @brief Universally Unique Identifier for the BLE characteristic. */
    const char* _charUUID;

    /** @brief Flag used to identify whether or not there is new unread data. */
    bool _newDataAvailable;

    /** @brief Holds latest received data. */
    std::vector<std::string> _data;

};



