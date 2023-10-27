//
// Created by matt on 27/10/23.
//

#pragma once
#include "Arduino.h"

#if !defined(ESP32)
    #error "This code was written for ESP32 - it will NOT run on anything else. I'm sorry?"
#endif

#define UPDATE_INTERVAL 1000
#define SERIAL_BOUD 115200




class Visor {
public:
    void setup();
    void update();

private:
    unsigned long _updateTimer = 0;

protected:

};

