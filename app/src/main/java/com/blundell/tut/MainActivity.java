package com.blundell.tut;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private TryUart tryUart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryUart = new TryUart();
        tryUart.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tryUart.onStart();
    }

    @Override
    protected void onStop() {
        tryUart.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        tryUart.onDestroy();
        super.onDestroy();
    }
}
