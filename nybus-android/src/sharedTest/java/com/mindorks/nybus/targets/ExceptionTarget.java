package com.mindorks.nybus.targets;

import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;

/**
 * Created by user on 07/09/17.
 */

public class ExceptionTarget implements Target {


    public ExceptionTarget() {
    }
    @Override
    public void register(String... channelID) {
        NYBus.get().register(this,channelID);
    }

    @Override
    public void unregister(String... channelID) {
        NYBus.get().unregister(this,channelID);
    }

    @Subscribe(channelId = "one")
    public void onEventForTypeOne(String value) {
        // only the instance of channel one should get this event
    }


}
