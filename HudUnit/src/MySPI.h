/**
 *  @file MySPI.h
 *  @brief Header file for class: MySPI
 *
 *  Part of Display library for project VISOR.
 *
 *  TODO more explanation would be nice :)
 *
 *  Very loosely based on Adafruit's GPX library, and <INCLUDE SOURCE FOR THE OTHER LIBRARY>.
 *  TODO LINKs:
 *
 *
 *  @author Matt (Martin) Hnizdo
 *  @date 27/10/2023
 *  @bug No known bugs.
 */


#pragma once

#include "Arduino.h"
#include <SPI.h>
#include <cstdint>

class MySPI {
public:

    /********************************************************
     *                       Constructors                   *
     ********************************************************/

    /**
     * @brief Default constructor for the MySPI class.
     */
    MySPI() = default;;

    /**
     * @brief Constructor for the MySPI class.
     *
     * Initializes the SPI communication pins.
     *
     * @param cs   Chip select pin number.
     * @param dc   Data/command pin number.
     * @param mosi Master Out Slave In pin number.
     * @param sck  Serial Clock pin number.
     * @param rst  Reset pin number.
     */
    MySPI(int8_t cs, int8_t dc, int8_t mosi, int8_t sck, int8_t rst);

    /**
     * @brief Default destructor for the MySPI class.
     */
    ~MySPI()= default;;


    /********************************************************
    *                            SETUP                      *
    ********************************************************/

    /**
     * @brief Initialize the SPI interface.
     * This is a pure virtual function and must be implemented by derived classes.
     */
    virtual void begin() = 0;

    /**
     * @brief Initializes the SPI interface and configures the related pins.
     */
    void initSPI() const;

    /********************************************************
     *                 Sending commands                     *
     ********************************************************/

// TODO comments would be nice :D
    void sendCommand(uint8_t commandByte, uint8_t *dataBytes, uint8_t numDataBytes);
// TODO comments would be nice :D
    void sendCommand(uint8_t commandByte, const uint8_t *dataBytes = nullptr, uint8_t numDataBytes = 0);
// TODO comments would be nice :D
    void sendCommand16(uint16_t commandWord, const uint8_t *dataBytes = nullptr, uint8_t numDataBytes = 0);



protected:
    /**
     * @brief Sends out an 8-bit value over SPI using bit-banging.
     *
     * Manually toggles the MOSI and SCK pins to emulate the behavior of an SPI hardware module.
     *
     * @param b The 8-bit value to send.
     */
    void spiWrite8(uint8_t b);

    /**
     * @brief Sends out an 16-bit value over SPI using bit-banging.
     *
     * Manually toggles the MOSI and SCK pins to emulate the behavior of an SPI hardware module.
     *
     * @param b The 16-bit value to send.
     */
    void spiWrite16(uint16_t b);

    /**
     * @brief Initiates an SPI communication by pulling the chip select (CS) pin low.
     */
    inline void startWrite() {
        SPI_CS_LOW();
    }

    /**
     * @brief Terminates an SPI communication by releasing the chip select (CS) pin to high.
     */
    inline void endWrite() {
        SPI_CS_HIGH();
    }

    /********************************************************
     *      Inline methods for setting pins HIGH / LOW      *
     ********************************************************/


    /**
     * @brief Sets CD pin to LOW and and introduces a brief delay.
     */
    inline void SPI_CS_LOW() const{
        digitalWrite(_cs, LOW);
    }

    /**
     * @brief Sets CS pin to HIGH and and introduces a brief delay.
     */
    inline void SPI_CS_HIGH() const {
        digitalWrite(_cs, HIGH);
    }

    /**
     * @brief Sets DC pin to LOW and and introduces a brief delay.
     */
    inline void SPI_DC_LOW() const {
        digitalWrite(_dc, LOW);
    }

    /**
     * @brief Sets DC pin to HIGH and and introduces a brief delay.
     */
    inline void SPI_DC_HIGH() const {
        digitalWrite(_dc, HIGH);
    }

    /**
     * @brief Sets SCK pin to LOW and and introduces a brief delay.
     */
    inline void SPI_SCK_LOW() const {
        digitalWrite(_sck, LOW);
        for (volatile uint8_t i = 0; i < 1; i++);
    }

    /**
     * @brief Sets SCK pin to HIGH and and introduces a brief delay.
     */
    inline void SPI_SCK_HIGH() const {
        digitalWrite(_sck, HIGH);
        for (volatile uint8_t i = 0; i < 1; i++);
    }

    /**
     * @brief Sets MOSI pin to LOW and and introduces a brief delay.
     */
    inline void SPI_MOSI_LOW() const{
        digitalWrite(_mosi, LOW);
        for (volatile uint8_t i = 0; i < 1; i++);
    }

    /**
     * @brief Sets MOSI pin to HIGH and and introduces a brief delay.
     */
    inline void SPI_MOSI_HIGH() const {
        digitalWrite(_mosi, HIGH);
        for (volatile uint8_t i = 0; i < 1; i++);
    }



private:
    /** @brief Chip select pin */
    int8_t _cs{};

    /** @brief Data/command pin */
    int8_t _dc{};

    /** @brief MOSI pin */
    int8_t _mosi{};

    /** @brief SCK pin */
    int8_t _sck{};

    /** @brief Reset pin */
    int8_t _rst{};

};
