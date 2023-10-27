/**
 *  @file Display.cpp
 *  @brief Implementation file for class: Display
 *
 *  Part of Display library for project VISOR.
 *
 *  @author Matt (Martin) Hnizdo
 *  @date 27/10/2023
 *  @bug No known bugs.
 */

#include "Display.h"

Display::Display(int16_t w, int16_t h, int8_t cs, int8_t dc, int8_t mosi, int8_t sck, int8_t rst)
        : MySPI(cs, dc, mosi, sck, rst)
        , WIDTH(w)
        , HEIGHT(h)
        , _width(w)
        , _height(h)
{}

void Display::begin() {
    initSPI();

    // Initialization Sequence
    sendCommand(SSD1331_CMD_DISPLAYOFF); // 0xAE
    sendCommand(SSD1331_CMD_SETREMAP);   // 0xA0

    sendCommand(0x72); // RGB Color

    sendCommand(SSD1331_CMD_STARTLINE); // 0xA1
    sendCommand(0x0);
    sendCommand(SSD1331_CMD_DISPLAYOFFSET); // 0xA2
    sendCommand(0x0);
    sendCommand(SSD1331_CMD_NORMALDISPLAY); // 0xA4
    sendCommand(SSD1331_CMD_SETMULTIPLEX);  // 0xA8
    sendCommand(0x3F);                      // 0x3F 1/64 duty
    sendCommand(SSD1331_CMD_SETMASTER);     // 0xAD
    sendCommand(0x8E);
    sendCommand(SSD1331_CMD_POWERMODE); // 0xB0
    sendCommand(0x0B);
    sendCommand(SSD1331_CMD_PRECHARGE); // 0xB1
    sendCommand(0x31);
    sendCommand(SSD1331_CMD_CLOCKDIV); // 0xB3
    sendCommand(0xF0); // 7:4 = Oscillator Frequency, 3:0 = CLK Div Ratio (A[3:0]+1 = 1..16)
    sendCommand(SSD1331_CMD_PRECHARGEA); // 0x8A
    sendCommand(0x64);
    sendCommand(SSD1331_CMD_PRECHARGEB); // 0x8B
    sendCommand(0x78);
    sendCommand(SSD1331_CMD_PRECHARGEC); // 0x8C
    sendCommand(0x64);
    sendCommand(SSD1331_CMD_PRECHARGELEVEL); // 0xBB
    sendCommand(0x3A);
    sendCommand(SSD1331_CMD_VCOMH); // 0xBE
    sendCommand(0x3E);
    sendCommand(SSD1331_CMD_MASTERCURRENT); // 0x87
    sendCommand(0x06);
    sendCommand(SSD1331_CMD_CONTRASTA); // 0x81
    sendCommand(0x91);
    sendCommand(SSD1331_CMD_CONTRASTB); // 0x82
    sendCommand(0x50);
    sendCommand(SSD1331_CMD_CONTRASTC); // 0x83
    sendCommand(0x7D);
    sendCommand(SSD1331_CMD_DISPLAYON); //--turn on oled panel
}

void Display::setAddrWindow(uint16_t x, uint16_t y, uint16_t w, uint16_t h) {
    //TODO magic numbers

    uint8_t x1 = x;
    uint8_t y1 = y;
    if (x1 > 95)
        x1 = 95;
    if (y1 > 63)
        y1 = 63;

    uint8_t x2 = (x + w - 1);
    uint8_t y2 = (y + h - 1);
    if (x2 > 95)
        x2 = 95;
    if (y2 > 63)
        y2 = 63;

    if (x1 > x2) {
        uint8_t t = x2;
        x2 = x1;
        x1 = t;
    }
    if (y1 > y2) {
        uint8_t t = y2;
        y2 = y1;
        y1 = t;
    }

    sendCommand(0x15); // Column addr set
    sendCommand(x1);
    sendCommand(x2);

    sendCommand(0x75); // Column addr set
    sendCommand(y1);
    sendCommand(y2);

    startWrite();
}


void Display::fillScreen(uint16_t color) {
    fillRect(0, 0, _width, _height, color);
}

void Display::fillRect(int16_t x, int16_t y, int16_t w, int16_t h, uint16_t color) {
    if (w && h) {   // Nonzero width and height?
        if (w < 0) {  // If negative width...
            x += w + 1; //   Move X to left edge
            w = -w;     //   Use positive width
        }
        if (x < _width) { // Not off right
            if (h < 0) {    // If negative height...
                y += h + 1;   //   Move Y to top edge
                h = -h;       //   Use positive height
            }
            if (y < _height) { // Not off bottom
                int16_t x2 = x + w - 1;
                if (x2 >= 0) { // Not off left
                    int16_t y2 = y + h - 1;
                    if (y2 >= 0) { // Not off top
                        // Rectangle partly or fully overlaps screen
                        if (x < 0) {
                            x = 0;
                            w = x2 + 1;
                        } // Clip left
                        if (y < 0) {
                            y = 0;
                            h = y2 + 1;
                        } // Clip top
                        if (x2 >= _width) {
                            w = _width - x;
                        } // Clip right
                        if (y2 >= _height) {
                            h = _height - y;
                        } // Clip bottom
                        startWrite();

                        setAddrWindow(x, y, w, h);
                        writeColor(color, (uint32_t)w * h);

                        endWrite();
                    }
                }
            }
        }
    }
}


void Display::writeColor(uint16_t color, uint32_t len) {

    if (!len)
        return; // Avoid 0-byte transfers

    while (len--) {
        for (uint16_t bit = 0, x = color; bit < 16; bit++) {
            if (x & 0x8000)
                SPI_MOSI_HIGH();
            else
                SPI_MOSI_LOW();
            SPI_SCK_HIGH();
            x <<= 1;
            SPI_SCK_LOW();
        }
    }
}

void Display::drawImage() {
    //good question :)
}

void Display::setTextColor(uint16_t color) {
    //TODO not null
    _textColor = color;
}

void Display::setTextFont(sFont *font) {
    // TODO NotNULL
    _textFont = font;
}

void Display::setTextBackgroundColor(uint16_t color) {
    // TODO NotNULL
    _textBgColor = color;
}

void Display::print(uint16_t x, uint16_t y, const char *pString) {
    print(x, y, pString, _textFont, _textBgColor, _textColor);
}

void Display::print(uint16_t x, uint16_t y, const char *pString, sFont *font, uint16_t bg_color, uint16_t text_color) {
    uint16_t Xpoint = x;
    uint16_t Ypoint = y;

    //TODO FIX check :)
//    if (Xstart > Paint.Width || Ystart > Paint.Height) {
//        //Debug("Paint_DrawString_EN Input exceeds the normal display range\r\n");
//        return;
//    }

    while (* pString != '\0') {
        //if X direction filled , reposition to(Xstart,Ypoint),Ypoint is Y direction plus the Height of the character
        if ((Xpoint + font->Width ) > _width ) {
            Xpoint = x;
            Ypoint += font->Height;
        }

        // If the Y direction is full, reposition to(Xstart, Ystart)
        if ((Ypoint + font->Height ) > _height ) {
            Xpoint = x;
            Ypoint = y;
        }
        drawChar(Xpoint, Ypoint, * pString, font, bg_color, text_color);

        //The next character of the address
        pString ++;

        //The next word of the abscissa increases the font of the broadband
        Xpoint += font->Width;
    }
}

void Display::drawChar(uint16_t x, uint16_t y, const char c, sFont *Font, uint16_t bg_color, uint16_t text_color) {

    uint16_t Page, Column;

    if (x > _width || y > _height) {
        //Debug("Paint_DrawChar Input exceeds the normal display range\r\n");
        return;
    }

    uint32_t Char_Offset = (c - ' ') * Font->Height * (Font->Width / 8 + (Font->Width % 8 ? 1 : 0));
    const unsigned char *ptr = &Font->table[Char_Offset];

    for (Page = 0; Page < Font->Height; Page++) {
        for (Column = 0; Column < Font->Width; Column++) {
            if (pgm_read_byte(ptr) & (0x80 >> (Column % 8))) {
                drawPixel(x + Column, y + Page, text_color);
            } else {
                drawPixel(x + Column, y + Page, bg_color);
            }

            //One pixel is 8 bits
            if (Column % 8 == 7) {
                ptr++;
            }
        }

        // Move to the next byte if Font's width isn't a multiple of 8
        if (Font->Width % 8 != 0) {
            ptr++;
        }
    }
}

void Display::drawPixel(int16_t x, int16_t y, uint16_t color) {
    if ((x >= 0) && (x < _width) && (y >= 0) && (y < _height)) {
        setAddrWindow(x, y, 1, 1);
        spiWrite16(color);
    }
}



