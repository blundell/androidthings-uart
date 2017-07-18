package com.blundell.tut;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private TryUart tryUart;
    private TryI2CPush tryI2cPush;
    private TryI2CPoll tryI2cPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryUart = new TryUart();
        tryUart.onCreate();

//        tryI2cPush = new TryI2CPush();
//        tryI2cPush.onCreate();
//
//        tryI2cPoll = new TryI2CPoll();
//        tryI2cPoll.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        tryI2cPoll.onStart();
        tryUart.onStart();
    }

    @Override
    protected void onStop() {
//        tryI2cPoll.onStop();
        tryUart.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        tryI2cPush.onDestroy();
        tryUart.onDestroy();
        super.onDestroy();
    }
}
