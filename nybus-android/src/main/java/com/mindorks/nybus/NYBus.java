/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mindorks.nybus;


import com.mindorks.nybus.androidScheduler.SchedulerProviderImplementation;
import com.mindorks.nybus.driver.NYBusDriver;
import com.mindorks.nybus.event.Channel;
import com.mindorks.nybus.finder.NYEventClassFinder;
import com.mindorks.nybus.finder.NYSubscribeMethodFinder;
import com.mindorks.nybus.logger.AndroidLogger;
import com.mindorks.nybus.logger.Logger;
import com.mindorks.nybus.publisher.NYPublisher;
import com.mindorks.nybus.scheduler.SchedulerProvider;
import com.mindorks.nybus.util.Utils;
import com.mindorks.nybus.utils.ListUtils;

import java.util.List;

/**
 * Created by Jyoti on 16/08/17.
 */

public class NYBus implements Bus {

    private static NYBus sNYBusInstance;

    static {
        if (!Utils.isUnitTest()) {
            NYBus.get().setSchedulerProvider(new SchedulerProviderImplementation());
        }
    }

    private NYBusDriver mNYBusDriver;

    private NYBus() {
        mNYBusDriver = new NYBusDriver(new NYPublisher(),
                new NYSubscribeMethodFinder(),
                new NYEventClassFinder(),
                new AndroidLogger());
    }

    public static NYBus get() {
        if (sNYBusInstance == null) {
            synchronized (NYBus.class) {
                if (sNYBusInstance == null) {
                    sNYBusInstance = new NYBus();
                }
            }
        }
        return sNYBusInstance;
    }

    @Override
    public void setSchedulerProvider(SchedulerProvider schedulerProvider) {
        mNYBusDriver.initPublishers(schedulerProvider);
    }

    @Override
    public void setLogger(Logger logger) {
        mNYBusDriver.setLogger(logger);
    }

    @Override
    public void register(Object object, String... channelIDs) {
        register(object, ListUtils.convertVarargsToList(channelIDs));
    }

    @Override
    public void register(Object object, List<String> channelId) {
        mNYBusDriver.register(object, channelId);
    }

    @Override
    public void unregister(Object object, String... channelIDs) {
        unregister(object, ListUtils.convertVarargsToList(channelIDs));
    }

    @Override
    public void unregister(Object object, List<String> channelId) {
        mNYBusDriver.unregister(object, channelId);
    }

    @Override
    public void post(Object object) {
        post(object, Channel.DEFAULT);
    }

    @Override
    public void post(Object object, String channelId) {
        mNYBusDriver.post(object, channelId);
    }

    @Override
    public boolean isRegistered(Object object, String... channelIDs) {
        return mNYBusDriver.isRegistered(object, ListUtils.convertVarargsToList(channelIDs));
    }

    @Override
    public void enableLogging() {
        mNYBusDriver.enableLogging();
    }
}
