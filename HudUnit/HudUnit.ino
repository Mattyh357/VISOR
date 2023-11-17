#include <Arduino.h>
#include "src/Visor.h"

Visor visor = Visor();

void setup() {
  visor.setup();
}

void loop() {
  visor.update();
}
