#include "SPI.h"
#include "WS2801.h"
/*****************************************************************************
Example sketch for driving WS2801 pixels
*****************************************************************************/

// Choose which 2 pins you will use for output.
// Can be any valid output pins.
int dataPin = 2;
int clockPin = 8;
// Don't forget to connect the ground wire to Arduino ground,
// and the +5V wire to a +5V supply

uint8_t rval=0; uint8_t gval=0; uint8_t bval=0; uint16_t posval = 0;

const int AMOUNT_OF_LIGHTS = 16*10;
const int MAX_RGB_VALUES = 3 * AMOUNT_OF_LIGHTS;
char input[MAX_RGB_VALUES];  // data cache
char current;     // cache for received char
int incount = 0;  // counter for writing to cache
boolean didread = false;
uint8_t oscR, oscG, oscB;

// Set the first variable to the NUMBER of pixels. 25 = 25 pixels in a row
WS2801 strip = WS2801(AMOUNT_OF_LIGHTS);

void setup() {
  Serial.begin(250000);


  oscR = 255;
  oscG = 255;
  oscB = 255;


  strip.begin();

  // Update LED contents, to start they are all 'off'
  strip.show();
}


void loop() {
  while (incount < MAX_RGB_VALUES) {
      rval=uint8_t(input[incount++]);
      gval=uint8_t(input[incount++]);
      bval=uint8_t(input[incount++]);
      posval=uint16_t(incount/3);
      
      setTintPixelColor(posval, Color(bval, gval, rval));
  }
  if (didread) {
     strip.show();
     didread = false;
  }
}


void setTintPixelColor(uint16_t i, uint32_t c) {
  uint8_t b = c & 0xff;
  c >>= 8;
  uint8_t g = c & 0xff;
  c >>= 8;
  uint8_t r = c & 0xff;

  if (r == 255 && g == 255 && b == 255) {
    //no tint effect, no calculations needed
  }
  else {
    //apply tint effect and SWAP color according to real cabeling
    r = r*(oscR+1) >> 8;
    g = g*(oscG+1) >> 8;
    b = b*(oscB+1) >> 8;
  }


strip.setPixelColor(i, r, g, b);

}

// Create a 24 bit color value from R,G,B
uint32_t Color(uint8_t b, uint8_t g, uint8_t r) {
  uint32_t c = b;
  c <<= 8;
  c |= g;
  c <<= 8;
  c |= r;
  return c;
}

void serialEvent(){
  while (Serial.available() > 0) {
    Serial.readBytes(input, MAX_RGB_VALUES);
    incount = 0;
    didread = true;
  }
}
