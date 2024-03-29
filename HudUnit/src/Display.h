/**
 *  @file Display.h
 *  @brief Header file for class: Display
 *
 *  Part of Display library for project VISOR.
 *  This class encapsulates functionalities for OLED display using SSD1331 chip. Inheriting from a custom SPI class
 *  for sending commands directly to the display. Provides methods for initialization of the display, drawing
 *  individual pixes, images, and text.
 *
 *  Very loosely based on Adafruit's GPX library:
 *  https://github.com/adafruit/Adafruit-GFX-Library
 *
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#pragma once

#include <cstdint>
#include "MySPI.h"
#include "SSD1331_CMD.h"
#include "Fonts.h"
#include <cstring>
#include "Images.h"

/** @brief Black color in RGB565 format. */
#define BLACK           0x0000

/** @brief White color in RGB565 format. */
#define WHITE           0xFFFF

/** @brief Blue color in RGB565 format. */
#define BLUE            0x001F

/** @brief Red color in RGB565 format. */
#define RED             0xF800

/** @brief Green color in RGB565 format. */
#define GREEN           0x07E0


class Display : public MySPI {
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
     * Initializes a Display object by setting up the raw and effective display dimensions.
     * Additionally, it sets the SPI pin configurations by calling the base `MySPI` constructor.
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

    /**
     * @brief Initializes the display with specific settings and configurations.
     *
     * This method prepares the SSD1331 OLED display for operation. It initiates the SPI
     * interface and then sends a series of commands to set up various display parameters.
     * The sequence includes setting up the color mode, duty cycle, power modes, clock settings,
     * precharge values, contrast levels, and ultimately turning on the OLED panel.
     *
     * @note This method is heavily inspired (yeah... copy & paste basically) by the Adafruit GPX library.
     */
    void begin() override;

    /********************************************************
    *                 Drawing and printing                  *
    *********************************************************/

    /**
     * @brief Set the address window for subsequent drawing.
     *
     * Creates a region on the display where the pixel data will can be written.
     *
     * @param x The starting x-coordinate of the window.
     * @param y The starting y-coordinate of the window.
     * @param w The width of the window.
     * @param h The height of the window.
     */
    void setAddrWindow(uint16_t x, uint16_t y, uint16_t w, uint16_t h);

    /**
     * @brief Fills entire display with specific color.
     *
     * @param color 16-bit color value for the text.
     */
    void fillScreen(uint16_t color);

    /**
     * @brief Fills a rectangular area with the specified color.
     *
     * This method fills a specified rectangular area on the screen with the provided color.
     * It takes into account the possible negative width or height values and adjusts the
     * rectangle's position and dimensions accordingly. It also ensures that the rectangle
     * doesn't exceed the screen boundaries.
     *
     * @param x The horizontal start coordinate of the rectangle.
     * @param y The vertical start coordinate of the rectangle.
     * @param w Width of the rectangle.
     * @param h Height of the rectangle.
     * @param color 16-bit color value to fill the rectangle with.
     */
    void fillRect(int16_t x, int16_t y, int16_t w, int16_t h, uint16_t color);

    /**
     * @brief Draws an image on the display with center alignment.
     *
     * Takes an image structure as input and draws it on the display. The image is scaled by a factor of 1 (no scaling)
     * and centered on the display based on the display's width and height. The method adjusts for a fixed Y-offset to
     * account for specific layout needs. It iterates through each pixel of the image, checking the corresponding bit in
     * the image's bitmap table and draws a pixel on the display as either black or white based on the bit's value.
     *
     * @param Image Pointer to the sImage structure containing image data, including width, height, and the bitmap table.
     */
    void drawImage(sImage *Image);


    // Text

    /**
     * @brief Sets the color used for text rendering.
     *
     * Sets the text color property which is used in subsequent text rendering operations.
     *
     * @param color 16-bit color value for the text.
     */
    void setTextColor(uint16_t color);

    /**
     * @brief Sets the font used for text rendering.
     *
     * Sets the text font property which is used in subsequent text rendering operations.
     *
     * @param font Pointer to the font structure to be used for text rendering.
     */
    void setTextFont(sFont *font);


    /**
    * @brief Sets the background color used for text rendering.
    *
    * Sets the text background color property which is used in subsequent text rendering operations.
    *
    * @param color 16-bit background color value for the text.
    */
    void setTextBackgroundColor(uint16_t color);

    /**
     * @brief Prints a string on the display at a specified position using the current font and colors.
     *
     * If X or Y coordinates are -1, text will be centered on that axis.
     *
     * This overload uses the currently set text font, text color, and background color to print the string.
     * If those haven't been set - some default ones I picked at random will be used :)
     *
     * @param x Starting x-coordinate of the string or -1 to center.
     * @param y Starting y-coordinate of the string or -1 to center.
     * @param pString Pointer to the string to be printed.
     */
    void print(int x, int y, const char *pString);

    /**
     * @brief Prints a string on the display at a specified position with a specified font and colors.
     *
     * This method prints characters one by one from the provided string, wrapping to the next line if needed.
     * If the text reaches the end of the screen, it will wrap around to the beginning position.
     *
     * Will override text font, and color with those provided.
     *
     * @param x Starting x-coordinate of the string.
     * @param y Starting y-coordinate of the string.
     * @param pString Pointer to the string to be printed.
     * @param font Pointer to the font structure used for printing.
     * @param bg_color Background color for the string.
     * @param text_color Color of the text to be printed.
     */
    void print(uint16_t x, uint16_t y, const char *pString, sFont *font, uint16_t bg_color, uint16_t text_color);



protected:

    /**
     * @brief Writes a specific color to the display for a given length.
     *
     * This function writes a single color value repeatedly to the display for a specified number of times.
     * If the length provided is zero, the function exits without performing any operation.
     *
     * @param color The 16-bit color value to be written to the display.
     * @param len The number of times the color should be written.
     */
    void writeColor(uint16_t color, uint32_t len);

    /**
     * @brief Draws a character on the display at the specified position.
     *
     * This function renders a given character using the provided font, background color, and text color
     * at the designated x and y coordinates. The character's pixels are determined by the font table.
     *
     * @param x The x-coordinate where the character's top-left corner should be placed.
     * @param y The y-coordinate where the character's top-left corner should be placed.
     * @param c The character to be rendered.
     * @param Font Pointer to the font data structure containing character sizes and glyph bitmaps.
     * @param bg_color The background color for the character.
     * @param text_color The color used to render the character's pixels.
     */
    void drawChar(uint16_t x, uint16_t y, char c, sFont *Font, uint16_t bg_color, uint16_t text_color);

    /**
     * @brief Draws a single pixel on the display at the specified position.
     *
     * This function sets a specific pixel at the x and y coordinates to the desired color.
     * If the provided coordinates are outside the boundaries of the display, the function
     * does not modify any pixel.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @param color The desired color for the pixel.
     */
    void drawPixel(int16_t x, int16_t y, uint16_t color);

private:

    /**
     * @brief Display width adjusted by the current rotation setting.
     */
    int16_t _width{};

    /**
     * @brief Display height adjusted by the current rotation setting.
     */
    int16_t _height{};


    /**
     * @brief Current font for printing text.
     */
    sFont* _textFont{};

    /**
     * @brief Current color used for text printing.
     */
    uint16_t _textColor{BLACK};

    /**
     * @brief Current background color for text printing.
     */
    uint16_t _textBgColor{WHITE};

};


