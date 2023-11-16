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
        _pCharacteristic->setValue(&data, sizeof(data));
        _pCharacteristic->notify();
    }
}

bool Bluetooth::isThereNewData() {
    _newDataAvailable = !(_pCharacteristic->getValue().empty());
    return _newDataAvailable;
}

std::string Bluetooth::getData() {
    std::string newData = _pCharacteristic->getValue();
    wipeData();
    _newDataAvailable = false;  // Reset flag after reading.

    return newData;
}

void Bluetooth::wipeData() {
    _pCharacteristic->setValue("");
}




// TESTING


void Bluetooth::onReceivedData(const uint8_t* data, size_t length) {
    if (length < 3) return; // Ensure there's enough data for index and checksum_rec

    // Extract index (first 2 bytes)
    uint16_t index = data[0] | (data[1] << 8);

    // Extract actual data (between index and checksum_rec)
    std::vector<uint8_t> actualData(data + 2, data + length - 1);

    Serial.print("Data bytes: ");
    for (auto byte : actualData) {
        Serial.printf("%02X ", byte);
    }
    Serial.println(); // Move to a new line after printing


    // checksum_rec (last byte)
    uint8_t checksum_rec = data[length - 1];
    uint8_t checksum_cal = calculateChecksum(actualData.data(), actualData.size());


    std::string msg = "index: " + std::to_string(index) + "- " +std::to_string(checksum_rec) + "==" + std::to_string(checksum_cal);
    Serial.println(msg.c_str());

    if (checksum_rec == checksum_cal) {
//        storeData(index, actualData);
        Serial.println("data ok");
        sendAcknowledgment(true);
    } else {
        // Handle error
        Serial.println("data error");
        sendAcknowledgment(false);
    }
}




uint8_t Bluetooth::calculateChecksum(const uint8_t* data, size_t length) {
    uint8_t calculatedChecksum = 0;
    Serial.print("Data bytes: ");
    for (size_t i = 0; i < length; ++i) {
        calculatedChecksum += data[i];
        Serial.printf("%02X ", data[i]); // Print each byte in hex format
    }
    Serial.println(); // New line after printing all bytes
    return calculatedChecksum;
}


void Bluetooth::sendAcknowledgment(bool isSuccessful) {
    delay(50); //just to be sure
    sendData(isSuccessful ? 0x01 : 0x00);
}