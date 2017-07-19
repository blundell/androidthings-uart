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
            try {
                byte[] buffer = new byte[8];
                while ((uart.read(buffer, buffer.length)) > 0) {
                    byte messageCode = buffer[0];
                    if (messageCode == (byte) 0xFC) {
                        byte gestureCode = buffer[1];
                        if (gestureCode == 0x01) {
                            Log.d("TUT", "Swipe Right");
                            ledStrip.nextColor();
                        } else if (gestureCode == 0x02) {
                            Log.d("TUT", "Swipe Left");
                            ledStrip.previousColor();
                        }
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
