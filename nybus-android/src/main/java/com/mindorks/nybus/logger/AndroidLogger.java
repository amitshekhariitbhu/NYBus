package com.mindorks.nybus.logger;

import android.util.Log;

import com.mindorks.nybus.BuildConfig;

/**
 * Created by anandgaurav on 12-09-2017.
 */

public class AndroidLogger implements Logger {

    @Override
    public void log(String value) {
        if (BuildConfig.DEBUG) {
            Log.d("NYBus", value);
        }
    }

}
