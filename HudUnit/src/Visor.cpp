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


    _bt = Bluetooth(BT_NAME, BT_SERVICE_UUID, BT_CHARACTERISTIC_UUID);
    _bt.begin(&_bt);

    _display = Display(DISPLAY_WIDTH, DISPLAY_HEIGHT, PIN_CS, PIN_DC, PIN_MOSI, PIN_SCK, PIN_RST);
    _display.begin();
    _display.fillScreen(BLACK);

    //
    _display.print(10, 10, "DC", &Font24, BLACK, WHITE);

    myMode = disconnected;

}


//

//  QR code :)
//
//    delay(1000);
//
//    _display.fillScreen(BLACK);
//
//
//    uint8_t myQRCode[841]; // Make sure this array has enough space
//    const char* myText = "HelloQRCode12345";
//
//    if (QRGen::encodeText(myText, myQRCode)) {
//
//        int16_t startX = 0;  // Starting X position of the QR code on the display
//        int16_t startY = 0;  // Starting Y position of the QR code on the display
//
//        uint8_t qrcodeSize = 21;  // Assuming QR version 1 (21x21)
//
//        int scaleFactor = 2;  // or 3 for 3x3 blocks
//
//
//        Serial.print("QR: ");
//
//        for (uint8_t y = 0; y < qrcodeSize; y++) {
//            for (uint8_t x = 0; x < qrcodeSize; x++) {
//                bool moduleSet = myQRCode[y * qrcodeSize + x];
//                _display.testForQR(startX + x, startY + y, moduleSet ? BLACK : WHITE);
//
//                Serial.print(moduleSet ? "1" : "0");
//            }
//        }
//
//        Serial.println("END");
//
//
//
//    } else {
//        Serial.println("Failed to generate QR code.");
//    }
//
//}

bool _bootingCompleted = false;

// modes: disconnected -> loading imaged -> loading_config-> working/lost_connection

void Visor::update() {
    if(_updateTimer > millis())
        return;

    _updateTimer = millis() + UPDATE_INTERVAL;

//    return; // TODO REMOVE :D

//    Serial.println("Running Update");
//    Serial.println(_bt.isConnected() ? "BT connected" : "BT Not connected");

    // lost connection handling
    if(myMode != disconnected && _bt.isConnected() == false) {
        // TODO should not print every loop.. just once
        // when I lose connection
        _display.fillScreen(BLACK);
        _display.print(10, 10, "lost", &Font24, BLACK, WHITE);
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
            _display.print(10, 10, "boot", &Font24, BLACK, WHITE);
            _bt.sendData(REQUEST_BOOT_DATA);
        }
        return;
    }
    else if (myMode == booting) {

        // TODO this should only run when actually booting :)
        // TODO percentage
        std::string status = std::to_string((_bt._totalChunks - _bt._remainingChunks)) + "/" + std::to_string(_bt._totalChunks);
        _display.print(10, 30, status.c_str(), &Font24, BLACK, WHITE);

        if(_bt.last_instruction == ALL_BOOT_DATA_SENT) {
            // TODO process boot data
            Serial.println("PROCESSING BOOT DATA");

            _imgProcessor = ImageProcessor(_bt._fileStorage);

            delay(1000);
            // TODO clear _fileStorage
            Serial.println("FINISHED processing");


            _bootingCompleted = true;
            myMode = working;
            _display.fillScreen(BLACK);
            _display.print(10, 10, "work", &Font24, BLACK, WHITE);
            _bt.sendData(BOOT_DATA_PROCESSED);
        }

        return;
    }
    else if (myMode == lostSignal) {
        if(_bt.isConnected() == true) {
            if(_bootingCompleted) {
                myMode = working;
                _display.fillScreen(BLACK);
                _display.print(10, 10, "w1", &Font24, BLACK, WHITE);
            }
            else{
                myMode = booting;

                _display.fillScreen(BLACK);
                _display.print(10, 10, "b1", &Font24, BLACK, WHITE);
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
            std::string text = "S:" + number;
            Serial.println(text.c_str());

            _display.fillScreen(BLACK);
            _display.print(10, 10, text.c_str(), &Font24, BLACK, WHITE);
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

            _display.fillScreen(BLACK);
            _display.drawImageTest(_imgProcessor.getImage(number));


//            _display.print(10, 10, text.c_str(), &Font24, BLACK, WHITE);
            return;
        }
        else {
            std::string number = std::to_string(_bt.getData());
            std::string text = "unknown ins:" + std::to_string(_bt.last_instruction) + "data: " + number;
            Serial.println(text.c_str());

        }

    }

//    _display.print(10, 10, String(i++).c_str(), &Font24, BLACK, WHITE);
//    _display.print(10, 30, String(i++).c_str(), &Font24, BLACK, WHITE);


//    _display.fillRect(22, 6, 52, 52, RED);
//    delay(2000);
//
//
//    _display.fillRect(22, 6, 52, 52, WHITE);
//    delay(2000);
//
//    _display.fillScreen(BLACK);
//    delay(2000);

}


