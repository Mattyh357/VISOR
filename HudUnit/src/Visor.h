/**
 * @file Visor.h
 * @brief Header for the Visor class, part of the VISOR project.
 *
 * The Visor class encapsulates functionalities for managing the overall operation of the VISOR device,
 * including display and Bluetooth communication. It initializes the system, handles the main update loop,
 * and processes specific commands received over Bluetooth to control the display output. This class
 * integrates the Display and Bluetooth functionalities to create a coherent user interface experience.
 *
 *  @note This class is specifically designed for the ESP32 microcontroller.
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#pragma once
#include "Arduino.h"
#include "Display.h"
#include "Bluetooth.h"
#include "NavIcons.h"


// Macros for system-wide constants
#define UPDATE_INTERVAL 500
#define SERIAL_BOUD 115200

// Pin definitions for the SPI interface to the display
#define PIN_CS     5  ///< Chip Select
#define PIN_DC    22  ///< Data/Command
#define PIN_MOSI  23  ///< Master Out Slave In
#define PIN_SCK   18  ///< Serial Clock
#define PIN_RST   19  ///< Reset

// Display resolution
#define DISPLAY_WIDTH  96 ///< Display width in pixels.
#define DISPLAY_HEIGHT 64 ///< Display height in pixels.

// Bluetooth settings
#define BT_SERVICE_UUID        "4fafc201-1fb5-459e-8fcc-c5c9c331914b"  ///< Bluetooth service UUID.
#define BT_CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"  ///< Bluetooth characteristic UUID.
#define BT_NAME "VISOR"      ///< Bluetooth device name.

#if !defined(ESP32)
#error "This code was written for ESP32 - it will NOT run on anything else. I'm sorry?"
#endif

class Visor {
public:

    /**
     * @brief Initializes the device, setting up serial communication, Bluetooth, and display components.
     *
     * This method starts serial communication at a predefined baud rate for debugging, initializes
     * the Bluetooth module with a specific name and UUIDs for service and characteristic, and sets up
     * the display including its dimensions and pin connections. It also sets default text properties
     * and clears the display, preparing the device for operation.
     */
    void setup();

    /**
     * @brief Main update loop, checks for Bluetooth connection and data, and updates the display.
     *
     * Periodically executed based on a predefined update interval, this method checks the Bluetooth
     * connection status and processes any new data received. It updates the display to show relevant
     * information or status indicators depending on whether the device is connected and if new data
     * has been received.
     */
    void update();

private:

    /**
     * @brief Handles the work mode display logic based on received data.
     *
     * This method interprets specific commands received via Bluetooth, updating the display
     * with relevant information or graphics. It supports different types of instructions, such as
     * displaying speed, or navigation icons.
     *
     * @param data A vector of strings containing the data received from Bluetooth.
     */
    void handle_workMode(std::vector<std::string> data);

    /**
     * @brief Handles the display logic for when the device is disconnected from Bluetooth.
     *
     * Clears the display and shows a "Disconnected" message along with a relevant icon.
     */
    void handle_disconnect();

    /**
     * @brief Clears the display if the new instruction differs from the last.
     *
     * To prevent unnecessary screen updates and flickering, this method checks if the current
     * instruction differs from the previous one. If so, it clears the display in preparation for
     * showing new information. This method helps in managing display transitions smoothly.
     *
     * @param instruction The current instruction received from Bluetooth.
     */
    void clearOnDifferentInstruction(int instruction);

    /**
     * @brief Bluetooth communication component of the VISOR device.
     */
    Bluetooth _bt;

    /**
     * @brief Display component of the VISOR device.
     */
    Display _display;

    /**
     * @brief Timer for managing update intervals.
     */
    unsigned long _updateTimer = 0;


    /**
     * @brief Last instruction received, used to detect changes in the display state.
     */
    int _lastInstruction;

};
