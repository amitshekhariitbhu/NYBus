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


import com.mindorks.nybus.AndroidScheduler.SchedulerProviderImplementation;
import com.mindorks.nybus.event.EventChannel;
import com.mindorks.nybus.internal.NYBusHandler;
import com.mindorks.nybus.scheduler.SchedulerProvider;

import java.util.ArrayList;

/**
 * Created by Jyoti on 16/08/17.
 */

public class NYBus {
    private static NYBus sNYBusInstance;
    private NYBusHandler mNYBusHandler;


    static {
        NYBus.get().setSchedulerProvider(new SchedulerProviderImplementation());
    }
    private NYBus() {
        // This class is not publicly instantiable
        mNYBusHandler = new NYBusHandler();
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

    public void setSchedulerProvider(SchedulerProvider schedulerProvider) {
        mNYBusHandler.setSchedulerProvider(schedulerProvider);
    }
    public void register(Object object) {
        ArrayList<String> defaultArrayList = new ArrayList<>();
        defaultArrayList.add(EventChannel.DEFAULT);
        register(object, defaultArrayList);
    }

    public void register(Object object, ArrayList<String> channelId) {
        mNYBusHandler.register(object, channelId);
    }

    public void unregister(Object object) {
        unregister(object, EventChannel.DEFAULT);
    }

    public void unregister(Object object, String channelId) {
        mNYBusHandler.unregister(object, channelId);
    }

    public void post(Object object) {
        post(object, EventChannel.DEFAULT);
    }

    public void post(Object object, String channelId) {
        mNYBusHandler.post(object, channelId);
    }


}
