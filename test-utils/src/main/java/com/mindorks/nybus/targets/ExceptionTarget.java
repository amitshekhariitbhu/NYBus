package com.mindorks.nybus.targets;

import com.mindorks.nybus.annotation.Subscribe;

/**
 * Created by user on 07/09/17.
 */

public class ExceptionTarget {

    public ExceptionTarget() {

    }

    @Subscribe(channelId = "one")
    public void onEventForTypeOne(String value) {
        // only the instance of channel one should get this event
    }

}
