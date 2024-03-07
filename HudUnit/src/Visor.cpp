//
// Created by matt on 27/10/23.
//

#include "Visor.h"

// TODO NOTE this whole file is a work in progress! or rather, it's currently ONLY USED FOR TESTING!!!!


enum operation_mode {
    disconnected,
    booting,
    working,
    lostSignal,
};


operation_mode myMode;

void Visor::setup() {
    Serial.begin(SERIAL_BOUD);
    Serial.println("App started");

    // Bluetooth
    _bt = Bluetooth(BT_NAME, BT_SERVICE_UUID, BT_CHARACTERISTIC_UUID);
    _bt.begin(&_bt);

    // Display
    _display = Display(DISPLAY_WIDTH, DISPLAY_HEIGHT, PIN_CS, PIN_DC, PIN_MOSI, PIN_SCK, PIN_RST);
    _display.begin();
    _display.fillScreen(BLACK);

    // Start with disconnected image
    _display.drawImageTest(MyImage(ImgDisconnected.Width, ImgDisconnected.Height, ImgDisconnected.toVector()));

    myMode = disconnected;



    // TEST CODE

}


bool _bootingCompleted = false;

// modes: disconnected -> loading imaged -> loading_config-> working/lost_connection

void Visor::update() {
    if(_updateTimer > millis())
        return;

    _updateTimer = millis() + UPDATE_INTERVAL;

//    return; // TODO remove :)

    // lost connection handling
    if(myMode != disconnected && myMode != lostSignal && _bt.isConnected() == false) {
        // TODO should not print every loop.. just once
        // when I lose connection
        _display.fillScreen(BLACK);
        _display.drawImageTest(MyImage(ImgDisconnected.Width, ImgDisconnected.Height, ImgDisconnected.toVector()));
        myMode = lostSignal;
        _updateTimer = 0;
        return;
    }

    // first thing...just wait for it to connect. and send request for boot data
    if(myMode == disconnected){
        if(_bt.isConnected() == true){
            // TODO keep trying to request it
            Serial.println("Connected - sending REQUEST_BOOT_DATA");
            delay(1000);
            myMode = booting;
            _display.fillScreen(BLACK);
            _display.drawImageTest(MyImage(ImgDisconnected.Width, ImgDisconnected.Height, ImgDisconnected.toVector()));
            _bt.sendData(REQUEST_BOOT_DATA);
        }
        return;
    }
    else if (myMode == booting) {
        // Print % of booting
        _display.print(10, 5, "BOOT", &Font24, BLACK, WHITE);

        std::string status = std::to_string((_bt._totalChunks - _bt._remainingChunks)) + "/" + std::to_string(_bt._totalChunks);
        _display.print(10, 30, status.c_str(), &Font24, BLACK, WHITE);

        // Booting completed
        if(_bt.last_instruction == ALL_BOOT_DATA_SENT) {
            Serial.println("PROCESSING BOOT DATA");
            _imgProcessor = ImageProcessor(_bt._fileStorage);

            delay(200);
            // TODO clear _fileStorage
            Serial.println("FINISHED processing");

            _bootingCompleted = true;
            myMode = working;
            _display.fillScreen(BLACK);
            _display.drawImageTest(MyImage(ImgStandBy.Width, ImgStandBy.Height, ImgStandBy.toVector()));
            _bt.sendData(BOOT_DATA_PROCESSED);
        }

        return;
    }
    else if (myMode == lostSignal) {
        if(_bt.isConnected() == true) {
            if(_bootingCompleted) {
                myMode = working;
                _display.fillScreen(BLACK);
                _display.drawImageTest(MyImage(ImgStandBy.Width, ImgStandBy.Height, ImgStandBy.toVector()));
            }
            else{
                myMode = booting;
                _display.fillScreen(BLACK);
                _display.print(10, 10, "b1", &Font24, BLACK, WHITE); // TODO replace with img
                _bt.sendData(REQUEST_BOOT_DATA);
            }

            _updateTimer = 0;
            return;
        }
    }


    //if I'm here, that meaning I'm in working state

    if(_bt.isThereNewData()) {

        Serial.print("New BT data received: ");

        if(_bt.last_instruction == SPEED_INSTRUCTION) {
            std::string number = std::to_string(_bt.getData());
            Serial.println(number.c_str());

            if(_prevPrintType != _bt.last_instruction){
                _display.fillScreen(BLACK);
                _prevPrintType = _bt.last_instruction;
            }

            _display.print(30, 15, number.c_str(), &Font24, BLACK, WHITE);
            return;
        }
        else if (_bt.last_instruction == NAVIGATION_IMG_INSTRUCTION) {
            uint16_t number = _bt.getData();

            Serial.print("nav test image: ");
            for (auto byte : _imgProcessor.getImage(number).getData()) {
                Serial.printf("%02X ", byte);
            }
            Serial.println(); // Move to a new line after printing

            std::string text = "I:" + std::to_string(number);
            Serial.println(text.c_str());

            if(_prevPrintType != _bt.last_instruction){
                _display.fillScreen(BLACK);
                _prevPrintType = _bt.last_instruction;
            }

            _display.drawImageTest(_imgProcessor.getImage(number));


            return;
        }
        else {
            std::string number = std::to_string(_bt.getData());
            std::string text = "unknown ins:" + std::to_string(_bt.last_instruction) + "data: " + number;
            Serial.println(text.c_str());

        }

    }
}


