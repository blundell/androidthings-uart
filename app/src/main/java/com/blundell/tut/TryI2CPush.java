package com.blundell.tut;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class TryI2CPush {

    private I2cDevice i2CBus;
    private Gpio i2cDataNotifyBus;

    public void onCreate() {
        PeripheralManagerService manager = new PeripheralManagerService();

        try {
            for (String s : manager.getI2cBusList()) {
                Log.d("TUT", s);
            }
            i2CBus = manager.openI2cDevice("I2C1", 0x10);

//            byte status = i2CBus.readRegByte(0x00);
//            Log.d("TUT", "Status " + status);
            byte registerVersion = i2CBus.readRegByte(0xFE);
            Log.d("TUT", "Register version : " + registerVersion);
            byte model = i2CBus.readRegByte(0xFF);
            Log.d("TUT", "Model : " + model);
            byte xpos = i2CBus.readRegByte(0x08);
            Log.d("TUT", "XPos : " + xpos);
            byte zpos = i2CBus.readRegByte(0x0A);
            Log.d("TUT", "ZPos : " + zpos);

        } catch (IOException e) {
            throw new IllegalStateException("can't setup i2c bus", e);
        }

        try {
            i2cDataNotifyBus = manager.openGpio("BCM18");
            i2cDataNotifyBus.setActiveType(Gpio.ACTIVE_HIGH);
            i2cDataNotifyBus.setDirection(Gpio.DIRECTION_IN);
            i2cDataNotifyBus.setEdgeTriggerType(Gpio.EDGE_BOTH);
            i2cDataNotifyBus.registerGpioCallback(onI2cDataAvailable);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private final GpioCallback onI2cDataAvailable = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                byte status = i2CBus.readRegByte(0x00);
                byte registerVersion = i2CBus.readRegByte(0xFE);
                byte model = i2CBus.readRegByte(0xFF);
                byte xpos = i2CBus.readRegByte(0x08);
                byte zpos = i2CBus.readRegByte(0x0a);
                Log.d("TUT", "Status " + status);
                Log.d("TUT", "Register version : " + registerVersion);
                Log.d("TUT", "Model : " + model);
                Log.d("TUT", "XPos : " + xpos);
                Log.d("TUT", "ZPos : " + zpos);

                byte gesture = i2CBus.readRegByte(0x04);
                Log.d("TUT", "New gesture! " + gesture);

                if (gesture == 0x01) {
                    Log.d("TUT", "Swipe Right");
                } else if (gesture == 0x02) {
                    Log.d("TUT", "Swipe Left");
                }

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            return true;
        }
    };

    public void onDestroy() {
        i2cDataNotifyBus.unregisterGpioCallback(onI2cDataAvailable);
    }
}
