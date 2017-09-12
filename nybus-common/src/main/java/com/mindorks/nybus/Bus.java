package com.mindorks.nybus;

import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.scheduler.SchedulerProvider;

import java.util.List;

/**
 * Created by anandgaurav on 12-09-2017.
 */

public interface Bus {

    void setSchedulerProvider(SchedulerProvider schedulerProvider);

    void setLogger(Logger logger);

    void register(Object object, String... channelIDs);

    void register(Object object, List<String> channelId);

    void unregister(Object object, String... channelIDs);

    void unregister(Object object, List<String> channelId);

    void post(Object object);

    void post(Object object, String channelId);

    boolean isRegistered(Object object, String... channelIDs);
}
