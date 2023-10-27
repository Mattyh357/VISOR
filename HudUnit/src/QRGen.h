//
// Created by matt on 27/10/23.
//

//https://github.com/ricmoo/QRCode

#pragma once

#include <cstdint>
#include <qrcode.h>

#define QR_RAW_BUFFER_SIZE_V1 56
#define QR_TRANSFORMED_BUFFER_SIZE_V1 841

class QRGen {
public:

    static bool encodeText(const char *data, uint8_t* outputQRCode) {
        QRCode qrcode;

        uint8_t qrcodeData[QR_RAW_BUFFER_SIZE_V1];
        qrcode_initText(&qrcode, qrcodeData, 1, 0, data);

        // Ensure outputQRCode has enough space, otherwise return false
        if (outputQRCode == nullptr) {
            return false;
        }

        // Transform the QR code into a binary format suitable for rendering
        uint16_t index = 0;
        for (uint8_t y = 0; y < qrcode.size; y++) {
            for (uint8_t x = 0; x < qrcode.size; x++) {
                outputQRCode[index] = qrcode_getModule(&qrcode, x, y) ? 1 : 0;
                index++;

                //this is cool.. ish...
                // I need to make it twice bigger so that I don't need to upscale it
                // I need method that takes the image size and draws it in the middle of the display
                // need it as bitmap with bytes instead of binary....

                //I would also love to know how is it possible to get 841 of these instead of 441 :D
                // cos I'm sure I f*ed something up... this SHOULD rly rly rly be 441 :D

            }
        }

        Serial.print("INDEX: ");
        Serial.println(index);

        return true; // QR code was generated and stored successfully
    };
};
