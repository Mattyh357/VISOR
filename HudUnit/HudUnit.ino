#include <Arduino.h>
#include "src/Visor.h"

Visor visor;

void setup() {
    visor.setup();
}

void loop() {
    visor.update();
}
