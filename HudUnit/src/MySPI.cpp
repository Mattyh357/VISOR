/**
 *  @file MyCli.cpp
 *  @brief Implementation file for class: MySPI
 *
 *  Part of Display library for project VISOR.
 *
 *  @author Matt (Martin) Hnizdo
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#include "MySPI.h"

MySPI::MySPI(int8_t cs, int8_t dc, int8_t mosi, int8_t sck, int8_t rst)
        : _dc(dc)
        , _cs(cs)
        , _mosi(mosi)
        , _sck(sck)
        , _rst(rst)
        {}

void MySPI::initSPI() const {

    // Pin setup
    pinMode(_cs, OUTPUT);
    digitalWrite(_cs, HIGH);

    pinMode(_dc, OUTPUT);
    digitalWrite(_dc, HIGH);

    pinMode(_mosi, OUTPUT);
    digitalWrite(_mosi, LOW);

    pinMode(_sck, OUTPUT);
    digitalWrite(_sck, LOW);


    pinMode(_rst, OUTPUT);
    digitalWrite(_rst, HIGH);
    delay(100);
    digitalWrite(_rst, LOW);
    delay(100);
    digitalWrite(_rst, HIGH);
    delay(200);
}


void MySPI::sendCommand(uint8_t commandByte, uint8_t *dataBytes, uint8_t numDataBytes) {
    startWrite();

    SPI_DC_LOW();
    spiWrite8(commandByte);

    SPI_DC_HIGH();
    for (int i = 0; i < numDataBytes; i++) {
        spiWrite8(*dataBytes);
        dataBytes++;
    }

    endWrite();
}

void MySPI::sendCommand(uint8_t commandByte, const uint8_t *dataBytes, uint8_t numDataBytes) {
    startWrite();

    SPI_DC_LOW();
    spiWrite8(commandByte);

    SPI_DC_HIGH();
    for (int i = 0; i < numDataBytes; i++) {
        spiWrite8(pgm_read_byte(dataBytes++));
    }

    endWrite();
}

void MySPI::sendCommand16(uint16_t commandWord, const uint8_t *dataBytes, uint8_t numDataBytes) {
    startWrite();

    if (numDataBytes == 0) {
        SPI_DC_LOW();
        spiWrite16(commandWord);
        SPI_DC_HIGH();
    }

    for (int i = 0; i < numDataBytes; i++) {
        SPI_DC_LOW();
        spiWrite16(commandWord);
        SPI_DC_HIGH();
        commandWord++;
        spiWrite16((uint16_t)pgm_read_byte(dataBytes++));
    }

    endWrite();
}

void MySPI::spiWrite8(uint8_t b) {
    for (uint8_t bit = 0; bit < 8; bit++) {
        if (b & 0x80)
            SPI_MOSI_HIGH();
        else
            SPI_MOSI_LOW();
        SPI_SCK_HIGH();
        b <<= 1;
        SPI_SCK_LOW();
    }
}

void MySPI::spiWrite16(uint16_t w) {
    for (uint8_t bit = 0; bit < 16; bit++) {
        if (w & 0x8000)
            SPI_MOSI_HIGH();
        else
            SPI_MOSI_LOW();
        SPI_SCK_HIGH();
        SPI_SCK_LOW();
        w <<= 1;
    }
}