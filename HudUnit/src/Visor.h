//
// Created by matt on 27/10/23.
//

#pragma once
#include "Arduino.h"
#include "Display.h"
#include "QRGen.h"

#include "Bluetooth.h"
#include "ImageProcessor.h"



#define UPDATE_INTERVAL 1000
#define SERIAL_BOUD 115200

#define PIN_CS     5  // Orange Wire (OCS)
#define PIN_DC    22  // Brown Wire (D/C)
#define PIN_MOSI  23  // Blue Wire (MOSI)
#define PIN_SCK   18  // White Wire (SCK)
#define PIN_RST   19  // Green Wire (RST)

#define DISPLAY_WIDTH  96 //
#define DISPLAY_HEIGHT 64 //

#define BT_SERVICE_UUID        "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define BT_CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define BT_NAME "VISOR"

#if !defined(ESP32)
#error "This code was written for ESP32 - it will NOT run on anything else. I'm sorry?"
#endif


//TODO move elsewhere


class Visor {
public:
    void setup();
    void update();

protected:

private:

    unsigned long _updateTimer = 0; // Meh

    Display _display;

    Bluetooth _bt;

    ImageProcessor _imgProcessor;


    int i = 0;

};

