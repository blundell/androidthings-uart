package com.blundell.tut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String GESTURE_SENSOR = "UART0";
    private static final byte MSG_CODE_GESTURE_EVENT = (byte) 0xFC;
    private static final byte GESTURE_CODE_SWIPE_RIGHT_EVENT = 0x01;
    private static final byte GESTURE_CODE_SWIPE_LEFT_EVENT = 0x02;

    private final LedStrip ledStrip = new LedStrip();

    private UartDevice bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ledStrip.setUp();

        try {
            PeripheralManagerService service = new PeripheralManagerService();
            bus = service.openUartDevice(GESTURE_SENSOR);
        } catch (IOException e) {
            throw new IllegalStateException(GESTURE_SENSOR + " cannot be connected to.", e);
        }

        try {
            bus.setBaudrate(115200);
            bus.setDataSize(8);
            bus.setParity(UartDevice.PARITY_NONE);
            bus.setStopBits(1);
        } catch (IOException e) {
            throw new IllegalStateException(GESTURE_SENSOR + " cannot be configured.", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            bus.registerUartDeviceCallback(onUartBusHasData);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot listen for input from " + GESTURE_SENSOR, e);
        }
    }

    private final UartDeviceCallback onUartBusHasData = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                byte[] buffer = new byte[2];
                while ((uart.read(buffer, buffer.length)) > 0) {
                    byte messageCode = buffer[0];
                    byte gestureCode = buffer[1];
                    if (messageCode != MSG_CODE_GESTURE_EVENT) {
                        continue;
                    }
                    if (gestureCode == GESTURE_CODE_SWIPE_RIGHT_EVENT) {
                        ledStrip.nextColor();
                    } else if (gestureCode == GESTURE_CODE_SWIPE_LEFT_EVENT) {
                        ledStrip.previousColor();
                    }
                }

            } catch (IOException e) {
                Log.e("TUT", "Cannot read device data.", e);
            }

            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.e("TUT", "ERROR " + error);
        }
    };

    @Override
    protected void onStop() {
        bus.unregisterUartDeviceCallback(onUartBusHasData);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ledStrip.tearDown();
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("TUT", GESTURE_SENSOR + " connection cannot be closed.", e);
        }
        super.onDestroy();
    }
}
