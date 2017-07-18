package com.blundell.tut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.IOException;

/**
 * https://github.com/pimoroni/mote/blob/master/python/library/mote/__init__.py
 */
public class LedsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();

        for (String device : service.getUartDeviceList()) {
            Log.d("TUT", "device " + device);
        }

        try {
            UartDevice driver = service.openUartDevice("UART6");

            driver.setBaudrate(115200);
            driver.setDataSize(8);
            driver.setParity(UartDevice.PARITY_NONE);
            driver.setStopBits(1);

            // configure_channel 1
            byte[] config = "mote".getBytes();
            driver.write(config, config.length);
            config = "c".getBytes();
            driver.write(config, config.length);
            config = new byte[]{1, 16, 0};
            driver.write(config, config.length);

            // write pixel
            byte[] bytes = "mote".getBytes();
            driver.write(bytes, bytes.length);
            bytes = "o".getBytes();
            driver.write(bytes, bytes.length);
            byte r = (byte) 255;
            byte g = (byte) 255;
            byte b = (byte) 255;
            bytes = new byte[]{
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF),
                (byte) (r & 0xFF), (byte) (g & 0xFF), (byte) (b & 0xFF)};
            driver.write(bytes, bytes.length);

            driver.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
