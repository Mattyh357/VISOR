//
// Created by matt on 27/10/23.
//

#include "Bluetooth.h"
#include <Arduino.h>


class MyCharacteristicCallbacks : public BLECharacteristicCallbacks {
private:
    Bluetooth* bluetoothInstance;
public:
    explicit MyCharacteristicCallbacks(Bluetooth* instance) : bluetoothInstance(instance) {}
    void onWrite(BLECharacteristic *pCharacteristic) override {
        std::string value = pCharacteristic->getValue();
        if (!value.empty()) {
            bluetoothInstance->onReceivedData((uint8_t*)value.data(), value.length());
        }
    }
};



class MyServerCallbacks: public BLEServerCallbacks {

    // This method is called when a client connects
    void onConnect(BLEServer* pServer) {
        Serial.println("Client connected");
    };

    // This method is called when a client disconnects
    void onDisconnect(BLEServer* pServer) {
        Serial.println("Client disconnected");

        // You can start advertising again
        pServer->startAdvertising();
    }
};


Bluetooth::Bluetooth(const char* deviceName, const char* serviceID, const char* charID)
        : _deviceName(deviceName)
        , _serviceUUID(serviceID)
        , _charUUID(charID)
        , _newDataAvailable(false)
        {
//            BLEDevice::deinit(); // TODO TESTING
            BLEDevice::init(deviceName);
            _pServer = BLEDevice::createServer();
            _pServer->setCallbacks(new MyServerCallbacks);
        }


//begin

void Bluetooth::begin(Bluetooth* instance) {
    BLEService* pService = _pServer->createService(_serviceUUID);
    _pCharacteristic = pService->createCharacteristic
            (
                _charUUID
                , BLECharacteristic::PROPERTY_READ
                | BLECharacteristic::PROPERTY_WRITE
                | BLECharacteristic::PROPERTY_NOTIFY
                | BLECharacteristic::PROPERTY_INDICATE
            );

    _pCharacteristic->addDescriptor(new BLE2902());  // Needed for notifications.
    _pCharacteristic->setCallbacks(new MyCharacteristicCallbacks(instance));
    pService->start();

    if(1 == 1) { // TODO math is hard!
        BLEAdvertising *pAdvertising = _pServer->getAdvertising();
        pAdvertising->start();
    }
    else
        setAdvertising(false);

}


bool Bluetooth::isConnected() {
    return _pServer->getConnectedCount() > 0;
}

void Bluetooth::setAdvertising(bool status) {
    if (status) {
        _pServer->getAdvertising()->start();
    } else {
        _pServer->getAdvertising()->stop();
    }
}

void Bluetooth::sendData(uint8_t data) {
    if(isConnected()) {
        Serial.printf("Sending %02X ", data);
        Serial.println();
        _pCharacteristic->setValue(&data, sizeof(data));
        _pCharacteristic->notify();
    }
}

bool Bluetooth::isThereNewData() {
    return _newDataAvailable;
}







// TESTING

void Bluetooth::onReceivedData(const uint8_t* data, size_t length) {

    if (length < 3) {
        Serial.println("PROBLEM");
        return; // TODO problem
    }


    //debug print all
    Serial.print("RECEIVED bytes: ");
    for (size_t i = 0; i < length - CHECKSUM_SIZE; ++i) {
        Serial.printf("%02X ", data[i]); // Print each byte in hex format
    }
    Serial.println(); // New line after printing all bytes


    //check checksum
    // TODO do the do do
    uint8_t checksum = data[length - CHECKSUM_SIZE];

    if (checksum != calculateChecksum(data, length)) {
        // Handle error
        Serial.println("data error");
        sendAcknowledgment(false);
        return;
    }


    // If we're currently receiving a multi-chunk file
    if (_remainingChunks > 0) {
        handleFileChunk(data, length);
        _remainingChunks--;

        Serial.print("Remaining chunks: ");
        Serial.println(_remainingChunks);
        if (_remainingChunks == 0) {
            Serial.println("CHUNKS ZERO");
            onCompleteReceivedData(IMG_FILE_INSTRUCTION, 1);  // Call when all chunks are received
        }
    }
    else {
        // First byte as instruction
        uint8_t instruction = data[0];
        uint16_t payload = static_cast<uint16_t>(data[1]) << 8 | static_cast<uint16_t>(data[2]);


        // TODO magic bytes
        switch (instruction) {
            case SPEED_INSTRUCTION:
                // processNumber(data + 1, length - 1);
                onCompleteReceivedData(SPEED_INSTRUCTION, payload);
                break;
            case NAVIGATION_IMG_INSTRUCTION:
                onCompleteReceivedData(NAVIGATION_IMG_INSTRUCTION, payload);
                break;
            case IMG_FILE_INSTRUCTION: // File Transfer
                _remainingChunks = payload;
                _totalChunks = payload;
                _fileStorage.clear(); // Clear any existing data
                _fileStorage.reserve(16000); // Reserve space for the file

                Serial.println(("Starting file transfer" + std::to_string(_remainingChunks)).c_str());
                break;

            case ALL_BOOT_DATA_SENT:
                onCompleteReceivedData(ALL_BOOT_DATA_SENT, payload);
                break;

            default:
                Serial.println("UNKNOWN INSTRUCTION!!!!");
                // Handle unknown instruction
                // TODO not sure tbh
                return;
                break;
        }
    }

    // I got here ... that means where ever was happening was supposed to

    sendAcknowledgment(true);

}

void Bluetooth::handleFileChunk(const uint8_t* data, size_t length) {
    // Extract index (first 2 bytes)

    // Extract actual data (between index and checksum_rec)
    std::vector<uint8_t> actualData(data + 2, data + length - 1);

//    Serial.print("processing bytes: ");
//    for (auto byte : actualData) {
//        Serial.printf("%02X ", byte);
//    }
//    Serial.println();

    _fileStorage.insert(_fileStorage.end(), actualData.begin(), actualData.end());
//    _fileStorage.insert(_fileStorage.end(), chunkData + 2, chunkData + length - 1);
}

uint8_t Bluetooth::calculateChecksum(const uint8_t* data, size_t length) {
    uint8_t calculatedChecksum = 0;
    for (size_t i = 0; i < length - CHECKSUM_SIZE; ++i) {
        calculatedChecksum += data[i];
    }
    return calculatedChecksum;
}


void Bluetooth::sendAcknowledgment(bool isSuccessful) {
    delay(50); //just to be sure
    sendData(isSuccessful ? 0x01 : 0x00);
}



void Bluetooth::onCompleteReceivedData(uint8_t instruction, uint16_t data) {
    _newDataAvailable = true;
    last_instruction = instruction;

    std::string msg = "COMPLETE MESSAGE: " + std::to_string(instruction) + " - " + std::to_string(data);
    Serial.println(msg.c_str());

    last_data = data;

    if(instruction == IMG_FILE_INSTRUCTION) {
        Serial.print("Complete Data bytes: ");
        for (auto byte : _fileStorage) {
            Serial.printf("%02X ", byte);
        }
        Serial.println(); // Move to a new line after printing
    }
}

uint16_t Bluetooth::getData() {
    _newDataAvailable = false;  // Reset flag after reading.
    return last_data;
}