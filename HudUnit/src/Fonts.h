//
// Created by matt on 27/10/23.
//

#pragma once

#include <Arduino.h>
#include <stdint.h>

typedef struct _tFont
{
    const uint8_t *table;
    uint16_t Width;
    uint16_t Height;

} sFont;

extern sFont Font24;
extern sFont Font48;
//extern sFONT Font20; TODO later