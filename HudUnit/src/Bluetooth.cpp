/**
 *  @file Bluetooth.cpp
 *  @brief Implementation file for class: Bluetooth
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#include "Bluetooth.h"

#include <utility>


/**
 * Custom characteristic callbacks to handle data written to a BLE characteristic.
 */
class MyCharacteristicCallbacks : public BLECharacteristicCallbacks {
private:

    /** @brief Reference to the main Bluetooth instance. */
    Bluetooth* bluetoothInstance;

public:

    /**
     * Constructor to initialize callbacks with a Bluetooth instance.
     * @param instance Reference to the Bluetooth class instance.
     */
    explicit MyCharacteristicCallbacks(Bluetooth* instance) : bluetoothInstance(instance) {}

    /**
     * Handles write operations to the characteristic.
    * Ignores writes smaller than 3 bytes, debug prints received data, processes valid data, and send acknowledgment.
    * @param pCharacteristic Pointer to the characteristic receiving the write.
    */
    void onWrite(BLECharacteristic *pCharacteristic) override {

        std::string value = pCharacteristic->getValue();
        size_t length = value.length();
        uint8_t* data = (uint8_t*)value.data();

        // ignoring <3 as app will always send at least 3
        if (value.length() < 3) {
            return;
        }

        // Debug print
        Serial.print("Received: ");
        for (int i = 0; i < value.length(); i++) {
            Serial.print(value[i]);
        }
        Serial.println();

        // Checksum
        if(Utils::checkChecksum(data, length, CHECKSUM_SIZE)){

            // Remove the last byte (checksum) from the value
            value.resize(value.length() - 1);

            std::vector<std::string> values = Utils::splitString(value);
            bluetoothInstance->onReceivedData(values);
            bluetoothInstance->sendAcknowledgment(true);
        }
        else {
            bluetoothInstance->sendAcknowledgment(false);
        }
    }
};



/**
 * Custom callbacks for BLE server to handle client connections and disconnections.
 */
class MyServerCallbacks: public BLEServerCallbacks {

    /**
     * Logs to serial and performs actions when a client connects.
     * @param pServer Pointer to the BLE server.
     */
    void onConnect(BLEServer* pServer) {
        Serial.println("Client connected");
    };

    /**
     * Logs to serial and restarts advertising when a client disconnects.
     * @param pServer Pointer to the BLE server.
     */
    void onDisconnect(BLEServer* pServer) {
        Serial.println("Client disconnected");
        // Start advertising again
        pServer->startAdvertising();
    }
};


Bluetooth::Bluetooth(const char* deviceName, const char* serviceID, const char* charID)
        : _serviceUUID(serviceID)
        , _charUUID(charID)
        , _newDataAvailable(false)
        {
            BLEDevice::init(deviceName);
            _pServer = BLEDevice::createServer();
            _pServer->setCallbacks(new MyServerCallbacks);
        }



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

    // Start advertising
    BLEAdvertising *pAdvertising = _pServer->getAdvertising();
    pAdvertising->start();
}


bool Bluetooth::isConnected() {
    return _pServer->getConnectedCount() > 0;
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

void Bluetooth::sendAcknowledgment(bool isSuccessful) {
    delay(25); //just to be sure
    sendData(isSuccessful ? 0x01 : 0x00);
}


void Bluetooth::onReceivedData(std::vector<std::string> data) {
    _newDataAvailable = true;
    _data = std::move(data);
}


std::vector<std::string> Bluetooth::getData() {
    _newDataAvailable = false;  // Reset flag after reading.
    return _data;
}


