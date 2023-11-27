/**
 *  @file Image.h
 *  @brief Header and implementation file for class: Image
 *
 *  Essentially a data-struct for an image. Holds all data required to draw the picture pixel by pixel.
 *
 *  TODO more explanation would be nice :)
 *  TODO Change name after testing if completed
 *  TODO change vector to array
 *
 *
 *  @author Matt (Martin) Hnizdo
 *  @date 17/11/2023
 *  @bug No known bugs.
 */

#pragma once

#include <cstdint>
#include <vector>
#include <string>
#include "Arduino.h"


class MyImage {
public:

    /********************************************************
    *                       Constructors                   *
    ********************************************************/

    /**
     * @brief Constructor for Image class
     *
     * @param width     Width of the image (in pixels)
     * @param height    Height of the image (in pixels)
     * @param data      Bytearray of the picture - TODO vector
     */
    MyImage(uint32_t width, uint32_t height, const std::vector<uint8_t>& data)
            : _width(width)
            , _height(height)
            , _data(data)
            {}

    //TODO destructor with cleanup


    /********************************************************
    *                 Getters and setters                   *
    ********************************************************/

    /**
     * @brief Getter for width
     *
     * @return Returns width of the image (in pixels)
     */
    uint32_t getWidth() const { return _width; }

    /**
     * @brief Getter for height
     *
     * @return Return height of the image (in pixels)
     */
    uint32_t getHeight() const { return _height; }

    /**
     * @brief Getter for image data
     *
     * @return Returns bytearray of the picture
     */
    const std::vector<uint8_t>& getData() const { return _data; }



private:
    /** @brief Bytearray of the image */
    std::vector<uint8_t> _data;

    /** @brief Width of the image */
    uint32_t _width;

    /** @brief Height of the image */
    uint32_t _height;


};