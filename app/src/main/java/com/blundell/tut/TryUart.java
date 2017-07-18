package com.blundell.tut;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class TryUart {

    private static final String UART_DEVICE = "UART0";

    private final LedStrip ledStrip = new LedStrip();

    private UartDevice bus;

    public void onCreate() {
        ledStrip.setUp();

        try {
            PeripheralManagerService service = new PeripheralManagerService();

            for (String s : service.getUartDeviceList()) {
                Log.d("TUT", s);
            }

            bus = service.openUartDevice(UART_DEVICE);
        } catch (IOException e) {
            throw new IllegalStateException(UART_DEVICE + " cannot be connected to.", e);
        }

        try {
            bus.setBaudrate(115200);
            bus.setDataSize(8);
            bus.setParity(UartDevice.PARITY_NONE);
            bus.setStopBits(1);
        } catch (IOException e) {
            throw new IllegalStateException(UART_DEVICE + " cannot be configured.", e);
        }

    }

    public void onStart() {
        try {
            bus.registerUartDeviceCallback(onUartBusHasData);
            Log.d("TUT", "Register callback");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot listen for input from " + UART_DEVICE, e);
        }
    }

    private final UartDeviceCallback onUartBusHasData = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            Log.d("TUT", "data available");
            try {
                byte[] buffer = new byte[32];
                int count;
                while ((count = uart.read(buffer, buffer.length)) > 0) {
                    Log.d("TUT", "read : " + count + " bytes from peripheral");

                    for (byte b : buffer) {
                        Log.d("TUT", "got: " + b);
                    }

                    Log.d("TUT", "Msg code " + buffer[0]);
                    Log.d("TUT", "Matching gesture would be " + (byte) 0xFC);

                    byte messageCode = buffer[0];
                    switch (messageCode) {
                        case (byte) 0xFF:
                            Log.d("TUT", "Message code: Pen Up");
                            break;
                        case (byte) 0xFE:
                            Log.d("TUT", "Message code: Ranges");
                            break;
                        case (byte) 0xFA:
                            Log.d("TUT", "Message code: X Coordinate");
                            break;
                        case (byte) 0xFB:
                            Log.d("TUT", "Message code: Y Coordinate");
                            break;
                        case (byte) 0xFC:
                            Log.d("TUT", "Message code: Gesture Event");

                            byte gestureCode = buffer[1];
                            if (gestureCode == 0x01) {
                                Log.d("TUT", "Swipe Right");
                                ledStrip.nextColor();
                            } else if (gestureCode == 0x02) {
                                Log.d("TUT", "Swipe Left");
                                ledStrip.previousColor();
                            }

                            break;
                        case (byte) 0xF1:
                            Log.d("TUT", "Message code: ID");
                            break;
                        default:
                            Log.d("TUT", "Unhandled msg code " + messageCode);
                    }
                }

            } catch (IOException e) {
                Log.e("TUT", "Cannot read device data.", e);
            }

            Log.d("TUT", "data consumed");
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.e("TUT", "ERROR " + error);
        }
    };

    public void onStop() {
        bus.unregisterUartDeviceCallback(onUartBusHasData);
    }

    public void onDestroy() {
        ledStrip.tearDown();
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("TUT", UART_DEVICE + " connection cannot be closed.", e);
        }
    }
}
