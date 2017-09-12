package com.mindorks.nybus.logger;

/**
 * Created by anandgaurav on 12-09-2017.
 */

public class JavaLogger implements Logger {

    @Override
    public void log(String value) {
        System.out.println(value);
    }

}
