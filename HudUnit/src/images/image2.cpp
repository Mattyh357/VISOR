

#include "../Images.h"

const uint8_t image2_map[] PROGMEM =
        {
                //Image: test
                //Size: 32x32

                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x30, 0x0C, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x7F, 0xFE, 0x00,
                0x00, 0xFF, 0xFF, 0x00,
                0x01, 0xC0, 0x03, 0x80,
                0x03, 0x80, 0x01, 0xC0,
                0x07, 0x00, 0x00, 0xE0,
                0x0E, 0x00, 0x00, 0x70,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x0C, 0x00, 0x00, 0x30,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00
        };

sImage Image2 = {
        image2_map,
        32, /* Width */
        32, /* Height */
};