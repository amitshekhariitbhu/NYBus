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
/**
 * Created by amitshekhar on 14/08/17.
 */

import com.mindorks.nybus.Scheduler.SchedulerProvider;

public class NYBus {
    private static NYBus sNYBusInstance;
    private NYBusHandler mNYBusHandler;

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


    private NYBus() {
        mNYBusHandler = new NYBusHandler();
    }

    public void setSchedulerProvider(SchedulerProvider schedulerProvider) {
        mNYBusHandler.setSchedulerProvider(schedulerProvider);
    }
}
