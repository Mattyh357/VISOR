#pragma once

#include <cstdint>
#include <vector>
#include <string>
#include "Arduino.h"


// TODO I don't rly want a vector tbh... but will deal with that later :)

class MyImage {

    std::vector<uint8_t> data;

public:
    MyImage(uint32_t width, uint32_t height, const std::vector<uint8_t>& data)
            : Width(width), Height(height), data(data) {}

    const std::vector<uint8_t>& getData() const { return data; }

    uint32_t Width, Height;
};