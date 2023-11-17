//
// Created by matt on 27/10/23.
//

#pragma once

#include <Arduino.h>
#include <stdint.h>

typedef struct _tImage
{
    const uint8_t *table;
    uint16_t Width;
    uint16_t Height;

} sImage;

extern sImage Image1;
extern sImage Image2;
//extern sFONT Font20; TODO later