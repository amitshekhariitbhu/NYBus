package com.mindorks.nybus.logger;

import android.util.Log;

/**
 * Created by anandgaurav on 12-09-2017.
 */

public class AndroidLogger implements Logger {

    @Override
    public void log(String value) {
        Log.d("NYBus", value);
    }

}
