cmake_minimum_required(VERSION 3.23)
project(HudUnit)

set(CMAKE_CXX_STANDARD 14)

# Fake definition just to stop CLion from complaining :)
add_definitions(-DESP32)

# Just to make CLion happy :)
include_directories(
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/tools/sdk/esp32s2/include/soc/esp32s2/include/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/tools/sdk/esp32s2/include/freertos/include/


        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/libraries/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/libraries/SPI/src/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/cores/esp32/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/libraries/BLE/src/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/tools/sdk/esp32/dout_qspi/include/
        /home/matt/.arduino15/packages/esp32/hardware/esp32/2.0.11/tools/sdk/esp32/include/bt/host/bluedroid/api/include/api/
)

include_directories(src)



add_executable(
        HudUnit
        src/Visor.cpp
        src/Visor.h
        HudUnit.ino
)
