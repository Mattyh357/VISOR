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

typedef struct Image
{
    /** @brief array with image data  */
    const uint8_t *table;

    /** @brief Width of the image  */
    uint16_t Width;

    /** @brief Height of the image  */
    uint16_t Height;


    /** TODO remove
     * Converts the array to a vector
     *
     * @return Bytearray representation of the image as a vector
     */
    std::vector<uint8_t> toVector() const {
        return std::vector<uint8_t>(table, table + Width * Height);
    }

} sImage;

extern sImage ImgDisconnected;
extern sImage ImgStandBy;

extern sImage Icon_00_straight;
extern sImage Icon_01_merge;
extern sImage Icon_02_ferry_train;
extern sImage Icon_03_ferry;
extern sImage Icon_04_turn_slight_left;
extern sImage Icon_05_turn_left;
extern sImage Icon_06_turn_sharp_left;
extern sImage Icon_07_ramp_left;
extern sImage Icon_08_fork_left;
extern sImage Icon_09_uturn_left;
extern sImage Icon_10_roundabout_left;
extern sImage Icon_11_turn_slight_right;
extern sImage Icon_12_turn_right;
extern sImage Icon_13_turn_sharp_right;
extern sImage Icon_14_ramp_right;
extern sImage Icon_15_fork_right;
extern sImage Icon_16_uturn_right;
extern sImage Icon_17_roundabout_right;
extern sImage Icon_18_destination;

