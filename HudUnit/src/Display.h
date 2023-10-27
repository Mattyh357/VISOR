/**
 *  @file Display.h
 *  @brief Header file for class: Display
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

#include <cstdint>
#include "MySPI.h"
#include "SSD1331_CMD.h"
#include "Fonts.h"


#define  BLACK          0x0000
#define WHITE           0xFFFF
#define BLUE            0x001F
#define RED             0xF800
#define GREEN           0x07E0
#define CYAN            0x07FF
#define MAGENTA         0xF81F
#define YELLOW          0xFFE0


class Display : public MySPI{
public:

    /********************************************************
     *                       Constructors                   *
    ********************************************************/

    /**
     * @brief Default constructor for the Display class.
     */
    Display() = default;

    /**
     * @brief Constructor for the Display class.
     *
     * TODO comment
     *
     * @param w   Width of the display.
     * @param h   Height of the display.
     * @param cs   Chip select pin number.
     * @param dc   Data/command pin number.
     * @param mosi Master Out Slave In pin number.
     * @param sck  Serial Clock pin number.
     * @param rst  Reset pin number.
     */
    Display(int16_t w, int16_t h, int8_t cs, int8_t dc, int8_t mosi, int8_t sck, int8_t rst);

    /**
     * @brief Default destructor for the Display class.
     */
    ~Display() = default;

    /********************************************************
    *                            SETUP                      *
    ********************************************************/

    //TODO comments
    void begin() override;

    /**
     * @brief Set the address window for subsequent drawing.
     * @param x The starting x-coordinate of the window.
     * @param y The starting y-coordinate of the window.
     * @param w The width of the window.
     * @param h The height of the window.
     */
    void setAddrWindow(uint16_t x, uint16_t y, uint16_t w, uint16_t h);


    //TODO meh?
//    int16_t width() const { return _width; };
//    int16_t height() const { return _height; };


    /********************************************************
    *                 Drawing and printing                  *
    *********************************************************/

    void fillScreen(uint16_t color);
    void fillRect(int16_t x, int16_t y, int16_t w, int16_t h, uint16_t color);

    //TODO comments
    void drawImage();

    // Text

    //TODO comments
    void setTextColor(uint16_t color);
    //TODO comments
    void setTextFont(sFont font);
    //TODO comments
    void setTextBackgroundColor(uint16_t color);

    //TODO comments
    void print(uint16_t x, uint16_t y, const char *pString);
    //TODO comments
    void print(uint16_t x, uint16_t y, const char *pString, sFont *Font);
    //TODO comments
    void print(uint16_t x, uint16_t y, const char *pString, sFont *Font, uint16_t Color_Background, uint16_t Color_Foreground);



protected:
    //TODO comments
    void writeFillRectPreclipped(int16_t x, int16_t y, int16_t w, int16_t h, uint16_t color);
    //TODO comments
    void writeColor(uint16_t color, uint32_t len);
    //TODO comments
    void drawChar(uint16_t x, uint16_t y, const char Acsii_Char, sFont *Font, uint16_t Color_Background, uint16_t Color_Foreground);
    //TODO comments
    void drawPixel(int16_t x, int16_t y, uint16_t color);

private:

    /**
     * @brief The raw display width which remains constant.
     */
    int16_t WIDTH;

    /**
     * @brief The raw display height which remains constant.
     */
    int16_t HEIGHT;

    /**
     * @brief Display width adjusted by the current rotation setting.
     */
    int16_t _width;

    /**
     * @brief Display height adjusted by the current rotation setting.
     */
    int16_t _height;

    /**
     * @brief Current display rotation ranging from 0 to 3. // TODO figure what will be what
     */
    uint8_t _rotation;

    /**
     * @brief Horizontal position (x-coordinate) where text printing starts.
     */
    int16_t _cursor_x;

    /**
     * @brief Vertical position (y-coordinate) where text printing starts.
     */
    int16_t _cursor_y;

    /**
     * @brief Current font for printing text.
     */
    sFont* _textFont;

    /**
     * @brief Current color used for text printing.
     */
    uint16_t _textcolor;

    /**
     * @brief Current background color for text printing.
     */
    uint16_t _textbgcolor;


};


