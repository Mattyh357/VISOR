//
// Created by matt on 27/10/23.
//

#include "Visor.h"

void Visor::setup() {
    Serial.begin(SERIAL_BOUD);
    Serial.print("App started");

}

void Visor::update() {
    if(_updateTimer > millis())
        return;

    _updateTimer = millis() + UPDATE_INTERVAL;

}
