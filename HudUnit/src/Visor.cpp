/**
 *  @file Visor.cpp
 *  @brief Implementation file for class: Visor
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#include "Visor.h"

void Visor::setup() {
    Serial.begin(SERIAL_BOUD);
    Serial.println("App started");

    // Bluetooth
    _bt = Bluetooth(BT_NAME, BT_SERVICE_UUID, BT_CHARACTERISTIC_UUID);
    _bt.begin(&_bt);

    // Display
    _display = Display(DISPLAY_WIDTH, DISPLAY_HEIGHT, PIN_CS, PIN_DC, PIN_MOSI, PIN_SCK, PIN_RST);
    _display.setTextBackgroundColor(BLACK);
    _display.setTextColor(WHITE);
    _display.setTextFont(&Font20);

    _display.begin();
    _display.fillScreen(BLACK);

    // Start as disconnected
    handle_disconnect();
}



void Visor::update() {
    if(_updateTimer > millis())
        return;
    _updateTimer = millis() + UPDATE_INTERVAL;

    // Disconnected
    if(_bt.isConnected() == false) {
        handle_disconnect();
        return;
    }

    // Connected and there might be something to display
    if(_bt.isThereNewData()) {
        handle_workMode(_bt.getData());
    }
}


void Visor::handle_disconnect() {
    clearOnDifferentInstruction(-1);

    _display.drawImage(&ImgDisconnected);
    _display.print(-1, 20, "Disconnected");
}

void Visor::handle_workMode(std::vector<std::string> data) {

    int instruction = std::stoi(data[0]);

    // Clear screen if needed
    clearOnDifferentInstruction(instruction);

    switch (instruction) {
        case INSTRUCTION_SPEED_TEST: {
            Serial.println("SPEED");
            std::string speed = data[1];
            std::string distance = data[2];

            _display.print(0, 15, "(s)", &Font8, BLACK, WHITE);
            _display.print(-1, 20, speed.c_str());

            _display.print(0, 45, "(d)", &Font8, BLACK, WHITE);
            _display.print(-1, 40, distance.c_str());

            break;
        }
        case INSTRUCTION_NAVIG_TEST: {
            uint8_t iconNumber = std::stoi(data[1]);
            std::string distance = data[2];

            _display.drawImage(NavIcon::getImage(iconNumber));

            _display.fillRect(30, 40, 70, 55, BLACK);
            _display.print(-1, 40, distance.c_str());
        }
            break;
        default:
            Serial.println("Unknown instruction");
            return;
    }
}

void Visor::clearOnDifferentInstruction(int instruction) {
    if(_lastInstruction != instruction){
        _display.fillScreen(BLACK);
        _lastInstruction = instruction;
    }
}


