package com.blundell.tut;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

class LedStrip {

    private static final String APA102_RGB_7_LED_SLAVE = "SPI0.0";

    private static final byte LED_START_FRAME = (byte) 0b11100000;
    private static final byte LED_BRIGHTNESS = (byte) 0b00000011;
    private static final byte LED_BRIGHT_START_BYTE = (byte) (LED_START_FRAME | LED_BRIGHTNESS);
    private static final int ZERO_BITS = 0b0;
    private static final int TRANSACTION_SIZE = 37;

    private SpiDevice bus;
    private int position;

    /**
     * Call in on create to connect to the rainbow hat leds
     */
    public void setUp() {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            bus = service.openSpiDevice(APA102_RGB_7_LED_SLAVE);
        } catch (IOException e) {
            throw new IllegalStateException(APA102_RGB_7_LED_SLAVE + " connection cannot be opened.", e);
        }
        try {
            bus.setMode(SpiDevice.MODE2);
//            bus.setFrequency(1_000_000); // 1Mhz
//            bus.setBitsPerWord(8);
//            bus.setBitJustification(true);
        } catch (IOException e) {
            throw new IllegalStateException(APA102_RGB_7_LED_SLAVE + " cannot be configured.", e);
        }
    }

    public void nextColor() {
        if (++position == Color.RAINBOW.length) {
            position = 0;
        }
        changeColor(Color.RAINBOW[position]);
    }

    public void previousColor() {
        if (--position < 0) {
            position += Color.RAINBOW.length;
        }
        changeColor(Color.RAINBOW[position]);
    }

    private void changeColor(Color color) {
        byte[] data = new byte[TRANSACTION_SIZE];
        for (int i = 0; i <= 3; i++) {
            data[i] = ZERO_BITS;
        }
        int p = 4;
        for (int i = 0; i < 7; i++) {
            data[p++] = LED_BRIGHT_START_BYTE;
            data[p++] = (byte) color.b;
            data[p++] = (byte) color.g;
            data[p++] = (byte) color.r;
        }
        for (int i = 32; i < TRANSACTION_SIZE; i++) {
            data[i] = ZERO_BITS;
        }

        try {
            bus.write(data, data.length);
        } catch (IOException e) {
            throw new IllegalStateException(APA102_RGB_7_LED_SLAVE + " cannot be written to.", e);
        }
    }

    /**
     * Call in on destroy to disconnect from the rainbow hat leds
     */
    public void tearDown() {
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("TUT", APA102_RGB_7_LED_SLAVE + "connection cannot be closed, you may experience errors on next launch.", e);
        }
    }

    private static final class Color {
        private static final Color RED = new Color(50, 0, 0);
        private static final Color YELLOW = new Color(50, 50, 0);
        private static final Color PINK = new Color(50, 10, 12);
        private static final Color GREEN = new Color(0, 50, 0);
        private static final Color PURPLE = new Color(50, 0, 50);
        private static final Color ORANGE = new Color(50, 22, 0);
        private static final Color BLUE = new Color(0, 0, 50);

        static final Color[] RAINBOW = {
            Color.RED, Color.YELLOW, Color.PINK,
            Color.GREEN,
            Color.PURPLE, Color.ORANGE, Color.BLUE
        };

        int r;
        int g;
        int b;

        private Color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
