/**
 *  @file MySPI.h
 *  @brief Header and implementation file for class: Utils
 *
 *  Utils class contains a collections of method used through the project that don't rly belong anywhere.
 *
 *  @date 01/03/2024
 *  @bug No known bugs.
 */

//
// Created by matty on 13-Mar-24.
//

#pragma once

#include <sstream>
#include <vector>
#include <string>

class Utils{
public:

    /**
     * @brief Validates the checksum of a given data array.
     *
     * Calculates the checksum of the data by summing up all bytes except for the last `checksum_size` bytes,
     * which represent the expected checksum. Then, it compares the calculated checksum against the expected
     * checksum at the end of the data array.
     *
     * @param data Pointer to the data array to validate.
     * @param length Total length of the data array, including the checksum.
     * @param checksum_size Size of the checksum in bytes.
     * @return True if the checksum does not match the expected checksum, false otherwise.
     */
    static bool checkChecksum(uint8_t* data, size_t length, uint8_t checksum_size) {

        uint8_t calculatedChecksum = 0;
        uint8_t checksum = data[length - checksum_size];

        for (size_t i = 0; i < length - checksum_size; ++i) {
            calculatedChecksum += data[i];
        }

        return calculatedChecksum == checksum;
    }

    /**
     * @brief Splits a string into a vector of strings based on a delimiter.
     *
     * Takes a single input string and splits it into multiple strings based on the ',' delimiter, placing
     * each substring into a vector.
     *
     * @param str String to be split.
     * @return Vector of split strings.
     */
    static std::vector<std::string> splitString(const std::string& str) {
        std::vector<std::string> vector;
        std::stringstream ss(str);
        std::string item;

        while (std::getline(ss, item, ',')) {
            vector.push_back(item);
        }
        return vector;
    }

};