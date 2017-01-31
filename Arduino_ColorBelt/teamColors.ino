// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

// Which pin on the Arduino is connected to the NeoPixels?
// On a Trinket or Gemma we suggest changing this to 1
#define PIN            4

// How many NeoPixels are attached to the Arduino?
#define NUMPIXELS      60

// When we setup the NeoPixel library, we tell it how many pixels, and which pin to use to send signals.
// Note that for older NeoPixel strips you might need to change the third parameter--see the strandtest
// example for more information on possible values.
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

int delayval = 500; // delay for half a second

void setup() {
  Serial.begin(9600);

  pixels.begin(); // This initializes the NeoPixel library.
}

int reds[2] = {0, 160};
int greens[2] = {0, 160};
int blues[2] = {153, 160};

void loop() {
  for (int j =0; j<2;j++) {
    for (int i=0;i<NUMPIXELS;i++){
      int x = (i + j)%2;
      Serial.print(x);
      pixels.setPixelColor(i, pixels.Color(reds[x],greens[x],blues[x])); //goes through each color of the rainbow
    }
    pixels.show();
    
    delay(delayval);
  }
}
