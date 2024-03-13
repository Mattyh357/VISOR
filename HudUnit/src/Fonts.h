/**
 *  @file Display.h
 *  @brief Header file for class: Display
 *
 *  Part of Display library for project VISOR.
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */
#pragma once

#include <Arduino.h>
#include <stdint.h>


/**
 * @struct sFont
 * @brief This structure represents a font.
 *
 * It contains a pointer to the font's bitmap table and dimensions of each character in the font.
 */
typedef struct _tFont
{
    /** @brief Pointer to the font's bitmap table. */
    const uint8_t *table;

    /** @brief Width of each character in pixels. */
    uint16_t Width;

    /** @brief Height of each character in pixels. */
    uint16_t Height;

} sFont;

/**
 * @var Font8
 * @brief Font with height of 8 pixels.
 */
extern sFont Font8;

/**
 * @var Font16
 * @brief Font with height of 16 pixels.
 */
extern sFont Font16;

/**
 * @var Font20
 * @brief Font with height of 20 pixels.
 */

extern sFont Font20;


/**
 * @var Font24
 * @brief Font with height of 24 pixels.
 */
extern sFont Font24;
