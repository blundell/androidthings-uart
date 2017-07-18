package com.blundell.tut;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class TryI2CPoll {

    private I2cDevice i2CBus;
    private Handler handler;

    public void onCreate() {
        handler = new Handler();

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
    }

    public void onStart() {
        handler.post(i2cPoll);
    }

    private final Runnable i2cPoll = new Runnable() {
        @Override
        public void run() {
            try {

                byte status = i2CBus.readRegByte(0x00);
                Log.d("TUT", "Status " + status);

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
            handler.postDelayed(this, 500);
        }
    };

    public void onStop() {
        handler.removeCallbacks(i2cPoll);
    }
}
