/**
 *  @file Images.h
 *  @brief Header file for class: Images
 *
 *  Struct to handle different hardcoded images
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#pragma once

#include <Arduino.h>
#include <stdint.h>
#include <vector>

typedef struct _tImage
{
    /** @brief array with image data  */
    const uint8_t *table;

    /** @brief Width of the image  */
    uint16_t Width;

    /** @brief Height of the image  */
    uint16_t Height;

    /**
     * Converts the array to a vector
     *
     * @return Bytearray representation of the image as a vector
     */
    std::vector<uint8_t> toVector() const {
        return std::vector<uint8_t>(table, table + Width * Height);
    }

} sImage;

extern sImage ImgDisconnected;
extern sImage ImgLostSignal;
extern sImage ImgStandBy;
