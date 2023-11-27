//
// Created by matt on 17/11/23.
//

#pragma once

#include <cstdint>
#include <vector>
#include <string>
#include "Arduino.h"
#include "MyImage.h"

// TODO change everything! :)
// TODO needs error handling ;)
// the problem is...that if the image would fail to load, I cannot simply skip it - as the index of the image is important
// TODO needs a lot of stuff  actually :D
// it's all a bit shite if I can be honest.....


class ImageProcessor {
public:

    ImageProcessor() {};

    //TODO fix! or just burn with fire ;)
    ImageProcessor(const std::vector<uint8_t>& fileData) {
//        if (fileData.size() < 12) {
//            throw std::runtime_error("File data too small");
//        }

        // Extract header information
        numImages = *reinterpret_cast<const uint32_t*>(fileData.data());
        width = *reinterpret_cast<const uint32_t*>(fileData.data() + 4);
        height = *reinterpret_cast<const uint32_t*>(fileData.data() + 8);

        std::string txt = "num: " + std::to_string(numImages) + " - w: " + std::to_string(width) + " - h: " + std::to_string(height);
        Serial.println(txt.c_str());


        // Calculate the size of an individual image
        uint32_t imageSize = width * height / 8;
        size_t currentPos = 12; // Start after the header TODO magic

        for (uint32_t i = 0; i < numImages; ++i) {
//            if (currentPos + imageSize > fileData.size()) {
//                throw std::runtime_error("Incomplete image data");
//            }

//            images.emplace_back(std::vector<uint8_t>(fileData.begin() + currentPos,
//                                                     fileData.begin() + currentPos + imageSize));

            images.emplace_back(width, height, std::vector<uint8_t>(fileData.begin() + currentPos,
                                                                    fileData.begin() + currentPos + imageSize));

            currentPos += imageSize;
        }
    }

    const MyImage& getImage(uint32_t index) const {
//        if (index >= images.size()) {
//            throw std::out_of_range("Image index out of range");
//        }
        return images[index];
    }

private:
    uint32_t numImages;
    uint32_t width;
    uint32_t height;
    std::vector<MyImage> images;

};




