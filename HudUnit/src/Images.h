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

/**
 * @struct Image
 * @brief This structure represents an image.
 *
 * It contains a pointer to the image's bitmap table and its dimensions.
 */
typedef struct Image
{
    /** @brief Pointer to the array with image data. */
    const uint8_t *table;

    /** @brief Width of the image in pixels. */
    uint16_t Width;

    /** @brief Height of the image in pixels. */
    uint16_t Height;

} sImage;


/** @brief Image representing a disconnected state. */
extern sImage ImgDisconnected;

/** @brief Image representing a standby state. */
extern sImage ImgStandBy;


/********************************************************
 *                       Navigation                     *
********************************************************/


/** @brief Image icon for a straight direction. */
extern sImage Icon_00_straight;

/** @brief Image icon for a merge direction. */
extern sImage Icon_01_merge;

/** @brief Image icon for ferry or train directions. */
extern sImage Icon_02_ferry_train;

/** @brief Image icon for a ferry direction. */
extern sImage Icon_03_ferry;

/** @brief Image icon for a slight left turn. */
extern sImage Icon_04_turn_slight_left;

/** @brief Image icon for a left turn. */
extern sImage Icon_05_turn_left;

/** @brief Image icon for a sharp left turn. */
extern sImage Icon_06_turn_sharp_left;

/** @brief Image icon for a left ramp direction. */
extern sImage Icon_07_ramp_left;

/** @brief Image icon for a left fork direction. */
extern sImage Icon_08_fork_left;

/** @brief Image icon for a left U-turn. */
extern sImage Icon_09_uturn_left;

/** @brief Image icon for a left roundabout. */
extern sImage Icon_10_roundabout_left;

/** @brief Image icon for a slight right turn. */
extern sImage Icon_11_turn_slight_right;

/** @brief Image icon for a right turn. */
extern sImage Icon_12_turn_right;

/** @brief Image icon for a sharp right turn. */
extern sImage Icon_13_turn_sharp_right;

/** @brief Image icon for a right ramp direction. */
extern sImage Icon_14_ramp_right;

/** @brief Image icon for a right fork direction. */
extern sImage Icon_15_fork_right;

/** @brief Image icon for a right U-turn. */
extern sImage Icon_16_uturn_right;

/** @brief Image icon for a right roundabout. */
extern sImage Icon_17_roundabout_right;

/** @brief Image icon for a destination. */
extern sImage Icon_18_destination;